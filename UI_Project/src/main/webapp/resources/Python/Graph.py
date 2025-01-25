# -*- coding: utf-8 -*-
import pandas as pd
import numpy as np
from sklearn.neighbors import KNeighborsRegressor
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from sklearn.metrics import mean_squared_error, r2_score
import matplotlib.pyplot as plt
import io
import base64
from datetime import datetime

def analyze_data(file_path, feature_column, target_column):
    # 1단계: 데이터 로드
    try:
        data = pd.read_csv(file_path, encoding='UTF-8')
    except FileNotFoundError:
        return "Error: 파일을 찾을 수 없습니다. 파일 경로를 확인하세요."
    except Exception as e:
        return f"Error: 데이터를 로드하는 중 오류가 발생했습니다: {e}"

    # 데이터에 열이 존재하는지 확인
    if feature_column not in data.columns or target_column not in data.columns:
        return f"Error: '{feature_column}' 또는 '{target_column}' 열이 데이터에 없습니다."

    # 2단계: 데이터 전처리
    X = data[[feature_column]].copy()
    y = data[target_column].copy()

    # feature_column 데이터 타입 처리
    feature_encoder = None
    original_x_values = None
    
    if X[feature_column].dtype == 'object':
        try:
            # 날짜형 문자열인지 확인
            X[feature_column] = pd.to_datetime(X[feature_column])
            is_datetime = True
        except:
            # 날짜형이 아닌 문자열은 레이블 인코딩
            is_datetime = False
            feature_encoder = LabelEncoder()
            original_x_values = X[feature_column].values  # 원본 값 저장
            X[feature_column] = feature_encoder.fit_transform(X[feature_column])
    else:
        is_datetime = pd.api.types.is_datetime64_any_dtype(X[feature_column])

    if is_datetime:
        X[feature_column] = X[feature_column].astype(np.int64) // 10**9

    # target_column 데이터 타입 처리
    if y.dtype == 'object':
        try:
            y = y.astype(float)
        except:
            le = LabelEncoder()
            y = le.fit_transform(y)

    # 데이터를 numpy 배열로 변환
    X = X.values
    y = y.values

    # 데이터 분리 (80% 학습, 20% 테스트)
    if original_x_values is not None:
        # 문자열 데이터의 경우 원본 값도 함께 분할
        X_train, X_test, y_train, y_test, orig_x_train, orig_x_test = train_test_split(
            X, y, original_x_values, test_size=0.2, random_state=42
        )
    else:
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42
        )

    # 3단계: 모델 학습 및 예측
    model = KNeighborsRegressor(n_neighbors=5)
    model.fit(X_train, y_train)
    predictions = model.predict(X_test)

    # 4단계: 결과 시각화
    plt.switch_backend('Agg')
    plt.rc('font', family='Malgun Gothic')
    plt.figure(figsize=(12, 6))

    # x축 값 설정
    if original_x_values is not None:
        x_values = orig_x_test  # 원본 문자열 값 사용
    elif is_datetime:
        x_values = [datetime.fromtimestamp(x[0]) for x in X_test]
    else:
        x_values = [x[0] for x in X_test]

    # 그래프 그리기
    plt.scatter(x_values, y_test, color='blue', label='실제 값', alpha=0.6)
    plt.scatter(x_values, predictions, color='red', label='예측 값', alpha=0.6)

    plt.title('KNN을 사용한 예측 결과')
    plt.xlabel(feature_column)
    plt.ylabel(target_column)
    plt.legend()
    plt.grid(True)

    # x축 레이블 처리
    if is_datetime or isinstance(x_values[0], str):
        plt.xticks(rotation=45)

    # 여백 조정
    plt.tight_layout()

    # 그래프를 이미지로 변환
    buffer = io.BytesIO()
    plt.savefig(buffer, format='png', bbox_inches='tight', dpi=300)
    buffer.seek(0)
    image_png = buffer.getvalue()
    buffer.close()
    plt.close()

    # 이미지를 base64로 인코딩
    graph = base64.b64encode(image_png).decode()

    return graph