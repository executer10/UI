package test;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Calculator {
	
	//계산기의 상태를 저장하기 위한 변수들 입니다.
	private int firstNum = 0;
	private int secondNum = 0;
	private String operator = "";
	
	public Calculator() {
		JFrame frame = new JFrame("Calculator"); //새로운 창을 생성합니다.
		JTextField textField = new JTextField(); //결과 및 입력을 표시하는 테스트 필드입니다. 
		
		JPanel panel = new JPanel();//버튼들을 담기 위한 패널입니다.
		panel.setLayout(new GridLayout(4,4));//페널에 4x4 그리드 레이아웃 설정
		
		ActionListener insertActionListener = e -> { 
            String input = e.getActionCommand();
            if (operator.isEmpty()) {
                firstNum *= 10;
                firstNum += Integer.parseInt(input);
                textField.setText(String.valueOf(firstNum));
            } else {
                secondNum *= 10;
                secondNum += Integer.parseInt(input);
                textField.setText(String.valueOf(secondNum));
            }
        };

        ActionListener operatorActionListener = e -> { 
            operator = e.getActionCommand();
            textField.setText(operator);
        };

        ActionListener calculateActionListener = e -> { 
            int result;
            switch (operator) {
                case "+":
                    result=firstNum+secondNum; break;
                case "-":
                    result=firstNum-secondNum; break;
                case "*":
                    result=firstNum*secondNum; break;
                case "/":
                    result=firstNum/secondNum; break;
                 default:
                     throw new IllegalArgumentException("Invalid operator");
             }
             textField.setText(String.valueOf(result));
             operator="";
             firstNum=result;   // 계산된 결과값을 첫번째 숫자로 설정
             secondNum=0;       // 두번째 숫자 초기화
         };

         for(int i=1;i<=9;i++){
              JButton button=new JButton(String.valueOf(i));
              button.addActionListener(insertActionListener);   // 버튼에 이벤트 리스너 연결
              panel.add(button);                                // 버튼을 패널에 추가
          }

          JButton zeroButton=new JButton("0");
          zeroButton.addActionListener(insertActionListener);
          panel.add(zeroButton);

          String[] ops={"*","/","+","-"};
          for(String op:ops){
               JButton button=new JButton(op);
               button.addActionListener(operatorActionListener);
               panel.add(button);
           }

           JButton eqButton=new JButton("=");
           eqButton.addActionListener(calculateActionListener);
           panel.add(eqButton);

           frame.getContentPane().add(textField,BorderLayout.NORTH);  
           frame.getContentPane().add(panel,BorderLayout.CENTER);    
   
           frame.pack();
           frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
           frame.setVisible(true);                                 
    }

    public static void main(String[] args) {
        new Calculator();   // 계산기 객체 생성
    }

}

