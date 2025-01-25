# -*- coding: utf-8 -*-
import pandas as pd
import numpy as np
import requests
from xgboost import XGBRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import root_mean_squared_error
from sklearn.preprocessing import StandardScaler
import xml.etree.ElementTree as ET
from datetime import datetime, timedelta
from concurrent.futures import ThreadPoolExecutor, as_completed
import logging
import json

# 설정 로그 파일과 로그 레벨, 포맷 지정
logging.basicConfig(
    filename='gas_dashboard.log',
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s'
)
logger = logging.getLogger(__name__)


class APIHandler:
    # API 키 설정
    API_KEY = "XZPBXENognYAK0otVrl3nDQfxT0i6CHLBuT37E+O7XyVmFuya1RQTTYdKATr6FL2e+83zvYzEOwGCvhqGxas9A=="

    @staticmethod
    def make_api_request(url, params, data_type="JSON"):
        """
        API 요청을 보내고 응답을 반환합니다.
        성공 시 JSON 또는 XML 데이터를 반환하고, 실패 시 None을 반환합니다.
        """
        try:
            response = requests.get(url, params=params)
            if response.status_code != 200:
                logger.error(f"API 호출 실패: {response.status_code}, 응답: {response.text}")
                return None
            return response.json() if data_type == "JSON" else ET.fromstring(response.content)
        except Exception as e:
            logger.error(f"API 요청 오류: {str(e)}")
            return None

    @staticmethod
    def get_gas_supply_data():
        """
        가스 공급 데이터를 API에서 가져와 처리한 후 데이터프레임으로 반환합니다.
        """
        base_url = "http://apis.data.go.kr/B551210/supplyValuePerformanceList/getSupplyValuePerformanceList"

        current_date = datetime.now()
        current_year = current_date.year
        previous_year = current_year - 1
        previous_month = current_date.month - 1 if current_date.month > 1 else 12
        if previous_month == 12:
            current_year -= 1

        logger.info(f"데이터 수집 기간: {previous_year}-01 ~ {current_year}-{str(previous_month).zfill(2)}")

        processed_items = []  # 개별 회사 데이터 저장
        national_items = []   # 전국 평균 데이터 저장

        # 전년도 데이터 수집
        for month in range(1, 13):
            params = {
                "serviceKey": APIHandler.API_KEY,
                "dataType": "JSON",
                "openYr": previous_year,
                "openMm": str(month).zfill(2)
            }
            data = APIHandler.make_api_request(base_url, params)
            if not data:
                continue

            items = data.get('response', {}).get('body', {}).get('items', {}).get('item', [])
            if not items:
                continue

            monthly_total = {
                'supply_value': 0,
                'max_kcal': 0,
                'min_kcal': 0,
                'max_mj': 0,
                'min_mj': 0,
                'count': 0
            }

            # 각 회사 데이터 처리
            for item in items:
                try:
                    company_data = {
                        'supply_value': (
                            float(item.get('splCalMaxKcal', 0)) + 
                            float(item.get('splCalMinKcal', 0))
                        ) / 2,
                        'month': int(item.get('splMonth', 0)),
                        'year': int(item.get('splYear', 0)),
                        'max_kcal': float(item.get('splCalMaxKcal', 0)),
                        'min_kcal': float(item.get('splCalMinKcal', 0)),
                        'max_mj': float(item.get('splCalMaxMj', 0)),
                        'min_mj': float(item.get('splCalMinMj', 0)),
                        'company': item.get('splcalCpnm', '')
                    }
                    processed_items.append(company_data)

                    # 월별 총합 계산
                    monthly_total['supply_value'] += company_data['supply_value']
                    monthly_total['max_kcal'] += company_data['max_kcal']
                    monthly_total['min_kcal'] += company_data['min_kcal']
                    monthly_total['max_mj'] += company_data['max_mj']
                    monthly_total['min_mj'] += company_data['min_mj']
                    monthly_total['count'] += 1

                except (ValueError, TypeError) as e:
                    logger.warning(f"데이터 처리 중 오류 발생: {str(e)}, item: {item}")
                    continue

            # 전국 평균 계산 및 저장
            if monthly_total['count'] > 0:
                national_items.append({
                    'supply_value': monthly_total['supply_value'] / monthly_total['count'],
                    'month': month,
                    'year': previous_year,
                    'max_kcal': monthly_total['max_kcal'] / monthly_total['count'],
                    'min_kcal': monthly_total['min_kcal'] / monthly_total['count'],
                    'max_mj': monthly_total['max_mj'] / monthly_total['count'],
                    'min_mj': monthly_total['min_mj'] / monthly_total['count'],
                    'company': '전국'
                })

        # 현재년도 초기 몇 개월 데이터 수집
        if current_year > previous_year:
            for month in range(1, previous_month + 1):
                params = {
                    "serviceKey": APIHandler.API_KEY,
                    "dataType": "JSON",
                    "openYr": current_year,
                    "openMm": str(month).zfill(2)
                }
                data = APIHandler.make_api_request(base_url, params)
                if not data:
                    continue

                items = data.get('response', {}).get('body', {}).get('items', {}).get('item', [])
                if not items:
                    continue

                monthly_total = {
                    'supply_value': 0,
                    'max_kcal': 0,
                    'min_kcal': 0,
                    'max_mj': 0,
                    'min_mj': 0,
                    'count': 0
                }

                for item in items:
                    try:
                        company_data = {
                            'supply_value': (
                                float(item.get('splCalMaxKcal', 0)) +
                                float(item.get('splCalMinKcal', 0))
                            ) / 2,
                            'month': int(item.get('splMonth', 0)),
                            'year': int(item.get('splYear', 0)),
                            'max_kcal': float(item.get('splCalMaxKcal', 0)),
                            'min_kcal': float(item.get('splCalMinKcal', 0)),
                            'max_mj': float(item.get('splCalMaxMj', 0)),
                            'min_mj': float(item.get('splCalMinMj', 0)),
                            'company': item.get('splcalCpnm', '')
                        }
                        processed_items.append(company_data)

                        # 월별 총합 업데이트
                        monthly_total['supply_value'] += company_data['supply_value']
                        monthly_total['max_kcal'] += company_data['max_kcal']
                        monthly_total['min_kcal'] += company_data['min_kcal']
                        monthly_total['max_mj'] += company_data['max_mj']
                        monthly_total['min_mj'] += company_data['min_mj']
                        monthly_total['count'] += 1

                    except (ValueError, TypeError) as e:
                        logger.warning(f"데이터 처리 중 오류 발생: {str(e)}, item: {item}")
                        continue

                # 전국 평균 계산 및 저장
                if monthly_total['count'] > 0:
                    national_items.append({
                        'supply_value': monthly_total['supply_value'] / monthly_total['count'],
                        'month': month,
                        'year': current_year,
                        'max_kcal': monthly_total['max_kcal'] / monthly_total['count'],
                        'min_kcal': monthly_total['min_kcal'] / monthly_total['count'],
                        'max_mj': monthly_total['max_mj'] / monthly_total['count'],
                        'min_mj': monthly_total['min_mj'] / monthly_total['count'],
                        'company': '전국'
                    })

        # 데이터가 없을 경우 로그 기록 후 빈 데이터프레임 반환
        if not processed_items:
            logger.error("처리된 데이터가 없음")
            return pd.DataFrame(), pd.DataFrame()

        # 개별 회사 데이터프레임 생성 및 평균값 계산
        company_df = pd.DataFrame(processed_items)
        company_df = company_df.groupby(['year', 'month', 'company']).agg({
            'supply_value': 'mean',
            'max_kcal': 'mean',
            'min_kcal': 'mean',
            'max_mj': 'mean',
            'min_mj': 'mean'
        }).reset_index()

        # 전국 데이터프레임 생성
        national_df = pd.DataFrame(national_items)
        return company_df, national_df

    @staticmethod
    def get_holiday_data():
        """
        공휴일 데이터를 API에서 가져와 처리한 후 데이터프레임으로 반환합니다.
        """
        base_url = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo"
        current_year = datetime.now().year
        holidays = []

        # 각 월별 공휴일 데이터 수집
        for month in range(1, 13):
            params = {
                "serviceKey": APIHandler.API_KEY,
                "solYear": current_year,
                "solMonth": str(month).zfill(2),
                "numOfRows": 100
            }
            root = APIHandler.make_api_request(base_url, params, data_type="XML")
            if root is None:
                continue

            items = root.findall('.//item')
            if not items:
                continue

            # 각 공휴일 날짜 파싱
            for item in items:
                date_str = item.find('locdate').text
                if date_str:
                    try:
                        date_obj = datetime.strptime(date_str, '%Y%m%d')
                        holidays.append({"date": date_obj, "holiday": 1})
                    except ValueError:
                        logger.warning(f"잘못된 날짜 형식: {date_str}")

        # 공휴일 데이터가 없을 경우 로그 기록 후 빈 데이터프레임 반환
        if not holidays:
            logger.error("공휴일 데이터 수집 실패")
            return pd.DataFrame(columns=['year', 'month', 'holiday_count'])

        # 데이터프레임 생성 및 월별 공휴일 수 집계
        holiday_df = pd.DataFrame(holidays)
        holiday_df = DataProcessor.extract_date_features(holiday_df)
        return holiday_df.groupby(['year', 'month']).size().reset_index(name='holiday_count')

    @staticmethod
    def get_weather_data():
        """
        날씨 데이터를 API에서 가져와 처리한 후 데이터프레임으로 반환합니다.
        """
        base_url = "http://apis.data.go.kr/1360000/AsosDalyInfoService/getWthrDataList"
        end_date = datetime.now() - timedelta(days=1)  # 어제 날짜
        start_date = end_date - timedelta(days=365)    # 1년 전 날짜

        # 주요 기상 관측소 ID와 이름 매핑
        MAJOR_STATIONS = {
            '108': '서울',
            '159': '부산',
            '143': '대구',
            '156': '광주',
            '133': '대전',
            '112': '인천',
            '119': '수원',
            '146': '전주',
            '152': '울산',
            '105': '강릉'
        }

        def fetch_weather_for_month(args):
            """
            특정 기간과 관측소 ID에 대한 날씨 데이터를 가져옵니다.
            """
            date_range, station_id = args
            start, end = date_range
            params = {
                "serviceKey": APIHandler.API_KEY,
                "numOfRows": "31",
                "pageNo": "1",
                "dataCd": "ASOS",
                "dateCd": "DAY",
                "startDt": start.strftime("%Y%m%d"),
                "endDt": end.strftime("%Y%m%d"),
                "stnIds": station_id,
                "dataType": "JSON"
            }
            data = APIHandler.make_api_request(base_url, params)
            if not data:
                return []

            items = data.get('response', {}).get('body', {}).get('items', {}).get('item', [])
            if not items:
                return []

            # 날짜 형식 변환
            for item in items:
                item['date'] = datetime.strptime(item.get('tm'), '%Y-%m-%d')
                item['station_id'] = station_id
            return items

        # 월별 날짜 범위 생성
        monthly_ranges = DataProcessor.process_date_range(start_date, end_date)
        all_data = []

        # 각 월과 관측소에 대한 작업 리스트 생성
        tasks = [(date_range, station_id)
                 for date_range in monthly_ranges
                 for station_id in MAJOR_STATIONS.keys()]

        # 병렬 처리로 날씨 데이터 수집
        with ThreadPoolExecutor(max_workers=10) as executor:
            future_to_task = {executor.submit(fetch_weather_for_month, task): task
                              for task in tasks}
            for future in as_completed(future_to_task):
                task = future_to_task[future]
                try:
                    data = future.result()
                    all_data.extend(data)
                except Exception as e:
                    date_range, station_id = task
                    logger.error(f"{date_range[0].strftime('%Y-%m')} {MAJOR_STATIONS.get(station_id)} 데이터 처리 오류: {str(e)}")

        # 날씨 데이터가 없을 경우 로그 기록 후 빈 데이터프레임 반환
        if not all_data:
            logger.error("날씨 데이터 수집 실패")
            return pd.DataFrame(columns=[
                'year', 'month', 'temp_mean', 'temp_min', 'temp_max',
                'humidity_mean', 'wind_mean', 'rain_sum'
            ])

        # 데이터프레임 생성 및 필요한 컬럼 변환
        weather_df = pd.DataFrame(all_data)
        weather_df = DataProcessor.extract_date_features(weather_df)

        try:
            # 컬럼 이름 매핑
            column_mapping = {
                'avgTa': 'temp_mean',
                'minTa': 'temp_min',
                'maxTa': 'temp_max',
                'avgRhm': 'humidity_mean',
                'avgWs': 'wind_mean',
                'sumRn': 'rain_sum'
            }

            # 필요한 컬럼 변환 및 숫자형으로 변경
            for old_col, new_col in column_mapping.items():
                if old_col in weather_df.columns:
                    weather_df[new_col] = pd.to_numeric(weather_df[old_col], errors='coerce')
                else:
                    weather_df[new_col] = np.nan

            # 월별 기상 데이터 집계
            agg_dict = {
                'temp_mean': 'mean',
                'temp_min': 'min',
                'temp_max': 'max',
                'humidity_mean': 'mean',
                'wind_mean': 'mean',
                'rain_sum': 'sum'
            }

            weather_df = (
                weather_df
                .groupby(['year', 'month', 'station_id'])
                .agg(agg_dict)
                .reset_index()
                .groupby(['year', 'month'])
                .agg(agg_dict)
                .reset_index()
            )

        except Exception as e:
            logger.error(f"날씨 데이터 처리 오류: {str(e)}")
            return pd.DataFrame(columns=[
                'year', 'month', 'temp_mean', 'temp_min', 'temp_max',
                'humidity_mean', 'wind_mean', 'rain_sum'
            ])

        return weather_df


class DataProcessor:
    @staticmethod
    def extract_date_features(df, date_column='date'):
        """
        날짜 컬럼에서 연도와 월을 추출하여 새로운 컬럼으로 추가합니다.
        """
        df['month'] = df[date_column].dt.month
        df['year'] = df[date_column].dt.year
        return df

    @staticmethod
    def process_date_range(start_date, end_date):
        """
        시작 날짜부터 끝 날짜까지 월별로 날짜 범위를 나눕니다.
        """
        monthly_ranges = []
        current_date = start_date
        while current_date <= end_date:
            month_start = current_date.replace(day=1)
            # 다음 달 첫 날에서 하루를 빼면 현재 달의 마지막 날
            month_end = (month_start + timedelta(days=32)).replace(day=1) - timedelta(days=1)
            if month_end > end_date:
                month_end = end_date
            monthly_ranges.append((month_start, month_end))
            # 다음 달로 이동
            current_date = (month_start + timedelta(days=32)).replace(day=1)
        return monthly_ranges

    @staticmethod
    def handle_missing_data(df, numeric_cols, fill_value=0):
        """
        결측 데이터를 처리합니다.
        - 숫자형 컬럼은 연도와 월별 평균으로 채웁니다.
        - 전체 평균이 NaN일 경우 지정된 값으로 채웁니다.
        """
        for col in numeric_cols:
            if col in df.columns:
                df[col] = df.groupby(['year', 'month'])[col].transform(lambda x: x.fillna(x.mean()))
                df[col] = df[col].fillna(df[col].mean() if df[col].mean() is not np.nan else fill_value)
        return df


class DataIntegrator:
    @staticmethod
    def integrate_data():
        """
        모든 데이터를 통합하여 최종 분석에 사용할 데이터프레임을 생성합니다.
        """
        # 가스 공급 데이터 가져오기
        company_data, national_data = APIHandler.get_gas_supply_data()
        if company_data.empty or national_data.empty:
            logger.error("가스 데이터 수집 실패")
            return None, None
        if len(company_data) == 0:
            logger.error("회사 데이터가 비어있음")
            return None, None

        # 공휴일 및 날씨 데이터 가져오기
        holiday_data = APIHandler.get_holiday_data()
        weather_data = APIHandler.get_weather_data()
        if weather_data.empty:
            logger.warning("날씨 데이터 없음")
            return None, None

        # 회사 데이터에 공휴일 및 날씨 데이터 병합
        company_merged = company_data.merge(holiday_data, on=['year', 'month'], how='left')
        company_merged = company_merged.merge(weather_data, on=['year', 'month'], how='left')

        # 전국 데이터에 공휴일 및 날씨 데이터 병합
        national_merged = national_data.merge(holiday_data, on=['year', 'month'], how='left')
        national_merged = national_merged.merge(weather_data, on=['year', 'month'], how='left')

        # 결측 데이터 처리
        numeric_cols = ['temp_mean', 'humidity_mean', 'wind_mean']
        for merged_data in [company_merged, national_merged]:
            merged_data = DataProcessor.handle_missing_data(merged_data, numeric_cols)
            merged_data['rain_sum'] = merged_data['rain_sum'].fillna(0)          # 강수량 결측치는 0으로 채움
            merged_data['holiday_count'] = merged_data['holiday_count'].fillna(0)  # 공휴일 수 결측치는 0으로 채움

        return company_merged, national_merged


class ModelHandler:
    @staticmethod
    def train_prediction_model(X, y, model_params=None):
        """
        주어진 데이터로 예측 모델을 학습합니다.
        - 데이터가 충분하면 검증 세트를 사용하여 RMSE를 계산합니다.
        - 데이터가 적으면 검증 없이 전체 데이터를 사용합니다.
        """
        if model_params is None:
            model_params = {
                'n_estimators': 100,
                'learning_rate': 0.05,
                'max_depth': 5,
                'random_state': 42
            }

        scaler = StandardScaler()
        X_scaled = scaler.fit_transform(X)  # 특성 스케일링

        train_rmse = 0.0

        if len(X) > 10:
            # 데이터 분할: 학습용과 검증용
            X_train, X_val, y_train, y_val = train_test_split(X_scaled, y, test_size=0.2, random_state=42)
            model = XGBRegressor(**model_params)
            model.fit(X_train, y_train, eval_set=[(X_val, y_val)], verbose=False)  # 모델 학습

            val_pred = model.predict(X_val)
            train_rmse = root_mean_squared_error(y_val, val_pred)  # 검증 RMSE 계산
        else:
            # 데이터가 적을 경우 전체 데이터로 학습
            model = XGBRegressor(**model_params)
            model.fit(X_scaled, y)

        return model, scaler, train_rmse

    @staticmethod
    def predict_gas_demand(merged_data):
        """
        통합된 데이터를 사용하여 가스 수요를 예측하고 결과를 반환합니다.
        """
        if merged_data is None or merged_data.empty:
            logger.error("입력 데이터가 비어있습니다.")
            return {"error": "입력 데이터가 비어있습니다"}

        # 전국 데이터를 연도와 월별로 집계
        national_data = merged_data.groupby(['year', 'month']).agg({
            'supply_value': 'mean',
            'temp_mean': 'first',
            'temp_min': 'first',
            'temp_max': 'first',
            'humidity_mean': 'first',
            'wind_mean': 'first',
            'rain_sum': 'first',
            'holiday_count': 'first'
        }).reset_index()

        company_data = merged_data.copy()

        def generate_next_12month_labels():
            """
            다음 12개월의 라벨을 생성합니다.
            """
            months = [
                "1월","2월","3월","4월","5월","6월",
                "7월","8월","9월","10월","11월","12월"
            ]
            now = datetime.now()
            current_idx = now.month - 1
            next_idx = (current_idx + 1) % 12

            result = []
            for i in range(12):
                label_idx = (next_idx + i) % 12
                result.append(months[label_idx])
            return result

        next_month_labels = generate_next_12month_labels()

        # 예측 결과를 저장할 딕셔너리 초기화
        output_data = {
            # 국가 및 지역별 예측 값과 모델 성능 지표
            "predictions": {
                "national": {"predicted_value": 0, "train_rmse": 0},  # 전국 예측 수요 및 RMSE
                "regional": {}  # 각 지역(회사)별 예측 수요 및 RMSE
            },
            # 현재 공급량, 예측 수요, 공급 마진 등 메트릭 정보
            "metrics": {
                "currentSupply": {"national": 0, "regional": {}},       # 현재 가스 공급량
                "predictedDemand": {"national": 0, "regional": {}},     # 예측된 가스 수요
                "supplyMargin": {"national": 0, "regional": {}}        # 공급 마진 (%)
            },
            # 시각화를 위한 차트 데이터
            "charts": {
                "demandForecast": {
                    "labels": next_month_labels,    # 다음 12개월 라벨
                    "values": [],                    # 예측 수요 값
                    "actualValues": []               # 실제 수요 값
                },
                "temperatureCorrelation": {
                    "temperature": [],              # 월별 평균 기온
                    "demand": []                    # 월별 가스 수요
                },
                "predictionError": {
                    "labels": next_month_labels,    # 예측 오차 그래프 라벨
                    "errors": {"national": []}      # 전국 예측 오차율
                },
                "regionalDemand": {
                    "labels": [],                    # 지역(회사) 이름
                    "values": []                     # 지역별 가스 수요 값
                }
            }
        }

        # 예측에 사용할 특성 목록
        features = [
            'month', 'year', 'temp_mean', 'temp_min', 'temp_max',
            'humidity_mean', 'wind_mean', 'rain_sum', 'holiday_count'
        ]

        try:
            # 전국 데이터로 모델 학습 및 예측
            X_national = national_data[features]
            y_national = national_data['supply_value']

            if len(X_national) < 2:
                return {"error": "전국 데이터가 너무 적습니다."}

            model_national, scaler_national, train_rmse_national = ModelHandler.train_prediction_model(X_national, y_national)
            X_national_scaled = scaler_national.transform(X_national)

            national_predictions = model_national.predict(X_national_scaled)
            national_actuals = y_national.values
            national_pred = model_national.predict(X_national_scaled[-1:])

            # 현재 공급량과 예측 수요량 계산
            current_supply_national = float(national_actuals[-1])
            predicted_demand_national = float(national_pred[0])
            supply_margin_national = ((current_supply_national - predicted_demand_national) / current_supply_national) * 100

            # 예측 오차 계산
            national_errors = []
            for pred, actual in zip(national_predictions[:-1], national_actuals[:-1]):
                if actual != 0:
                    error_rate = ((actual - pred) / actual) * 100
                else:
                    error_rate = 0
                national_errors.append(round(error_rate, 2))
            if national_errors:
                national_errors.append(national_errors[-1])  # 마지막 값 반복

            # 결과 데이터에 추가
            output_data["charts"]["predictionError"]["errors"]["national"] = national_errors
            output_data["predictions"]["national"]["predicted_value"] = round(predicted_demand_national, 2)
            output_data["predictions"]["national"]["train_rmse"] = round(train_rmse_national, 2)
            output_data["metrics"]["currentSupply"]["national"] = round(current_supply_national, 2)
            output_data["metrics"]["predictedDemand"]["national"] = round(predicted_demand_national, 2)
            output_data["metrics"]["supplyMargin"]["national"] = round(supply_margin_national, 2)

            # 각 회사별로 모델 학습 및 예측
            unique_companies = company_data['company'].unique()
            for company in unique_companies:
                X_company = company_data[company_data['company'] == company][features]
                y_company = company_data[company_data['company'] == company]['supply_value']

                if len(X_company) > 1:
                    model_company, scaler_company, train_rmse_company = ModelHandler.train_prediction_model(X_company, y_company)
                    X_company_scaled = scaler_company.transform(X_company)
                    company_predictions = model_company.predict(X_company_scaled)
                    company_actuals = y_company.values
                    company_pred = model_company.predict(X_company_scaled[-1:])

                    # 현재 공급량과 예측 수요량 계산
                    current_supply = float(company_actuals[-1])
                    predicted_demand = float(company_pred[0])
                    supply_margin = ((current_supply - predicted_demand) / current_supply) * 100

                    # 예측 오차 계산
                    company_errors = []
                    for pred, actual in zip(company_predictions[:-1], company_actuals[:-1]):
                        if actual != 0:
                            err = ((actual - pred) / actual) * 100
                        else:
                            err = 0
                        company_errors.append(round(err, 2))
                    if company_errors:
                        company_errors.append(company_errors[-1])  # 마지막 값 반복

                    # 결과 데이터에 추가
                    output_data["predictions"]["regional"][company] = {
                        "predicted_value": round(predicted_demand, 2),
                        "train_rmse": round(train_rmse_company, 2)
                    }
                    output_data["metrics"]["currentSupply"]["regional"][company] = round(current_supply, 2)
                    output_data["metrics"]["predictedDemand"]["regional"][company] = round(predicted_demand, 2)
                    output_data["metrics"]["supplyMargin"]["regional"][company] = round(supply_margin, 2)
                    output_data["charts"]["predictionError"]["errors"][company] = company_errors

            # 수요 예측 차트 데이터 준비
            national_data_sorted = national_data.sort_values(["year", "month"]).reset_index(drop=True)
            total_points = len(national_data_sorted)
            if total_points < 11:
                historical_data = national_data_sorted["supply_value"].tolist()
            else:
                historical_data = national_data_sorted["supply_value"].iloc[-11:].tolist()

            current_prediction = [round(predicted_demand_national, 2)]
            output_data["charts"]["demandForecast"]["values"] = historical_data + current_prediction
            output_data["charts"]["demandForecast"]["actualValues"] = historical_data + [None]

            # 온도와 수요의 상관관계 데이터
            output_data["charts"]["temperatureCorrelation"]["temperature"] = national_data_sorted["temp_mean"].tolist()
            output_data["charts"]["temperatureCorrelation"]["demand"] = national_data_sorted["supply_value"].tolist()

            # 지역별 수요 데이터 준비
            latest_year = int(company_data['year'].max())
            latest_data = company_data[company_data['year'] == latest_year]
            recent_data = latest_data.groupby('company')['supply_value'].mean()
            output_data["charts"]["regionalDemand"]["labels"] = recent_data.index.tolist()
            output_data["charts"]["regionalDemand"]["values"] = recent_data.values.tolist()

        except Exception as e:
            logger.error(f"예측 중 오류: {str(e)}")
            return {"error": str(e)}

        return output_data


def main():
    """
    메인 함수:
    - 데이터를 통합하고
    - 예측 모델을 학습 및 예측하여
    - 결과를 JSON 형식으로 반환합니다.
    """
    try:
        company_data, _ = DataIntegrator.integrate_data()
        result_dict = ModelHandler.predict_gas_demand(company_data)
        return json.dumps(result_dict, ensure_ascii=False)
    except Exception as e:
        # 예외 발생 시 로그 기록 및 오류 메시지 반환
        logger.error("An error occurred during processing", exc_info=True)
        err_dict = {"error": str(e), "message": "An internal error occurred"}
        return json.dumps(err_dict, ensure_ascii=False, indent=2)


if __name__ == "__main__":
    print(main())
