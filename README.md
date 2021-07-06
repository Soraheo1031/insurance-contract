![image](https://user-images.githubusercontent.com/84304043/123741777-95f67700-d8e5-11eb-9c91-089f3049d77f.png)
![image](https://user-images.githubusercontent.com/84304043/123741876-c3432500-d8e5-11eb-9902-5acf7f305446.png)

# 보험가입청약

본 프로젝트는 MSA/DDD/Event Storming/EDA 를 포괄하는 분석/설계/구현/운영 전단계를 커버하도록 구성하였습니다.
이는 클라우드 네이티브 애플리케이션의 개발에 요구되는 체크포인트들을 통과하기 위한 답안을 포함합니다.
- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW


# Table of contents

- [보험가입청약](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 프로그래밍 / 폴리글랏 퍼시스턴스](#폴리글랏-프로그래밍-/-폴리글랏-퍼시스턴스)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
    - [Gateway](#Gateway)
    - [CQRS](#CQRS)
    - [Correlation](#Correlation)
  - [운영](#운영)
    - [CI/CD 설정](#CI/CD설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
    - [Self-healing (Liveness Probe)](#Self-healing-(Liveness-Probe))
    - [ConfigMap 사용](#ConfigMap-사용)
  - [신규 개발 조직의 추가](#신규-개발-조직의-추가)

# 서비스 시나리오

기능적 요구사항
1. 고객이 보험 상품을 선택하여 보험에 가입(청약)한다.
1. 청약 신청과 동시에 보험료 결제가 진행된다.
1. 보험료 결제가 완료되면 청약 신청이 완료된다.
1. 청약 신청이 완료되면 심사자가 배정된다.
1. 고객은 청약 신청을 취소할 수 있다. (단, 심사자 배정전까지만 가능. 심사Id로 구분)
1. 심사자가 승인하면 보험 계약이 체결된다. 
1. 심사자가 거절하면 보험료 결제가 취소된다.
1. 고객은 보험 청약에 대한 정보 및 청약 진행 상태를 확인 할 수 있다.


비기능적 요구사항
1. 트랜잭션
    1. 보험료 결제가 되지 않은 보험 청약 신청건은 보험 청약이 성립되지 않는다 - Sync 호출 
1. 장애격리
    1. 보험 심사 기능이 수행되지 않더라도 보험 청약 신청은 365일 24시간 받을 수 있어야 한다 - Async (event-driven), Eventual Consistency
    1. 보험 청약 시스템이 과중되면 사용자를 잠시동안 받지 않고 보험 청약 신청을  잠시후에 하도록 유도한다 - Circuit breaker, fallback
1. 성능
    1. 고객이 보험 청약 진행 상태를 확인할 수 있어야 한다 - CQRS


# 체크포인트

- 분석 설계


  - 이벤트스토밍: 
    - 스티커 색상별 객체의 의미를 제대로 이해하여 헥사고날 아키텍처와의 연계 설계에 적절히 반영하고 있는가?
    - 각 도메인 이벤트가 의미있는 수준으로 정의되었는가?
    - 어그리게잇: Command와 Event 들을 ACID 트랜잭션 단위의 Aggregate 로 제대로 묶었는가?
    - 기능적 요구사항과 비기능적 요구사항을 누락 없이 반영하였는가?    

  - 서브 도메인, 바운디드 컨텍스트 분리
    - 팀별 KPI 와 관심사, 상이한 배포주기 등에 따른  Sub-domain 이나 Bounded Context 를 적절히 분리하였고 그 분리 기준의 합리성이 충분히 설명되는가?
      - 적어도 3개 이상 서비스 분리
    - 폴리글랏 설계: 각 마이크로 서비스들의 구현 목표와 기능 특성에 따른 각자의 기술 Stack 과 저장소 구조를 다양하게 채택하여 설계하였는가?
    - 서비스 시나리오 중 ACID 트랜잭션이 크리티컬한 Use 케이스에 대하여 무리하게 서비스가 과다하게 조밀히 분리되지 않았는가?
  - 컨텍스트 매핑 / 이벤트 드리븐 아키텍처 
    - 업무 중요성과  도메인간 서열을 구분할 수 있는가? (Core, Supporting, General Domain)
    - Request-Response 방식과 이벤트 드리븐 방식을 구분하여 설계할 수 있는가?
    - 장애격리: 서포팅 서비스를 제거 하여도 기존 서비스에 영향이 없도록 설계하였는가?
    - 신규 서비스를 추가 하였을때 기존 서비스의 데이터베이스에 영향이 없도록 설계(열려있는 아키택처)할 수 있는가?
    - 이벤트와 폴리시를 연결하기 위한 Correlation-key 연결을 제대로 설계하였는가?

  - 헥사고날 아키텍처
    - 설계 결과에 따른 헥사고날 아키텍처 다이어그램을 제대로 그렸는가?
    
- 구현
  - [DDD] 분석단계에서의 스티커별 색상과 헥사고날 아키텍처에 따라 구현체가 매핑되게 개발되었는가?
    - Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 데이터 접근 어댑터를 개발하였는가
    - [헥사고날 아키텍처] REST Inbound adaptor 이외에 gRPC 등의 Inbound Adaptor 를 추가함에 있어서 도메인 모델의 손상을 주지 않고 새로운 프로토콜에 기존 구현체를 적응시킬 수 있는가?
    - 분석단계에서의 유비쿼터스 랭귀지 (업무현장에서 쓰는 용어) 를 사용하여 소스코드가 서술되었는가?
  - Request-Response 방식의 서비스 중심 아키텍처 구현
    - 마이크로 서비스간 Request-Response 호출에 있어 대상 서비스를 어떠한 방식으로 찾아서 호출 하였는가? (Service Discovery, REST, FeignClient)
    - 서킷브레이커를 통하여  장애를 격리시킬 수 있는가?
  - 이벤트 드리븐 아키텍처의 구현
    - 카프카를 이용하여 PubSub 으로 하나 이상의 서비스가 연동되었는가?
    - Correlation-key:  각 이벤트 건 (메시지)가 어떠한 폴리시를 처리할때 어떤 건에 연결된 처리건인지를 구별하기 위한 Correlation-key 연결을 제대로 구현 하였는가?
    - Message Consumer 마이크로서비스가 장애상황에서 수신받지 못했던 기존 이벤트들을 다시 수신받아 처리하는가?
    - Scaling-out: Message Consumer 마이크로서비스의 Replica 를 추가했을때 중복없이 이벤트를 수신할 수 있는가
    - CQRS: Materialized View 를 구현하여, 타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이) 도 내 서비스의 화면 구성과 잦은 조회가 가능한가?

  - 폴리글랏 플로그래밍
    - 각 마이크로 서비스들이 하나이상의 각자의 기술 Stack 으로 구성되었는가?
    - 각 마이크로 서비스들이 각자의 저장소 구조를 자율적으로 채택하고 각자의 저장소 유형 (RDB, NoSQL, File System 등)을 선택하여 구현하였는가?
  - API 게이트웨이
    - API GW를 통하여 마이크로 서비스들의 집입점을 통일할 수 있는가?
    - 게이트웨이와 인증서버(OAuth), JWT 토큰 인증을 통하여 마이크로서비스들을 보호할 수 있는가?
- 운영
  - SLA 준수
    - 셀프힐링: Liveness Probe 를 통하여 어떠한 서비스의 health 상태가 지속적으로 저하됨에 따라 어떠한 임계치에서 pod 가 재생되는 것을 증명할 수 있는가?
    - 서킷브레이커, 레이트리밋 등을 통한 장애격리와 성능효율을 높힐 수 있는가?
    - 오토스케일러 (HPA) 를 설정하여 확장적 운영이 가능한가?
    - 모니터링, 앨럿팅: 
  - 무정지 운영 CI/CD (10)
    - Readiness Probe 의 설정과 Rolling update을 통하여 신규 버전이 완전히 서비스를 받을 수 있는 상태일때 신규버전의 서비스로 전환됨을 siege 등으로 증명 
    - Contract Test :  자동화된 경계 테스트를 통하여 구현 오류나 API 계약위반를 미리 차단 가능한가?


# 분석/설계


## AS-IS 조직 (Horizontally-Aligned)
  ![image](https://user-images.githubusercontent.com/84304043/123742878-5df03380-d8e7-11eb-98ec-638a58ed87fa.png)

## TO-BE 조직 (Vertically-Aligned)
  ![image](https://user-images.githubusercontent.com/84304043/123743018-8f68ff00-d8e7-11eb-99f8-0bc9e2ee5865.png)

## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과: http://www.msaez.io/#/storming/uCvXrfqC2fOWn8AJ8j86klfXCNx1/b3143aae2bf54e75dc2ce9b31a545dae


### Event 도출
  ![image](https://user-images.githubusercontent.com/84304043/124086256-d55ec800-da8b-11eb-8cd0-4440aea01e7e.png)

### 부적격 Event 탈락
  ![image](https://user-images.githubusercontent.com/84304043/124086280-dc85d600-da8b-11eb-9d7a-e3df0dd4225a.png)

    - 과정중 도출된 잘못된 도메인 이벤트들을 걸러내는 작업을 수행함
        - 보험 상품 정보 등록됨, 보험 상품 정보 수정됨: 시나리오에도 없고 상품등록시스템(InnoProduct 등)으로 대체가능함
        - 보험가입내역조회됨: 상태(state) 변경을 발생시키지 않음
        - 보험 가입 심사 취소됨: 심사가 시작되면 가입 취소는 청약철회 프로세스로 다른 시스템에서 진행하여야 함

### Policy, Actor, Command 도출
  ![image](https://user-images.githubusercontent.com/84304043/124086778-5d44d200-da8c-11eb-8379-a658aa90a722.png)

###  Aggregate 도출(View추가)
  ![image](https://user-images.githubusercontent.com/84304043/124086909-76e61980-da8c-11eb-94ab-a3f0c1979650.png)

    - 보험가입, 결제, 심사는 그와 연결된 command와 event들에 의하여 트랜잭션이 유지되어야 하는 단위로 그들 끼리 묶어줌

### Bounded Context 도출
  ![image](https://user-images.githubusercontent.com/84304043/124086990-8bc2ad00-da8c-11eb-917b-fb2ab3518b2f.png)

    - 도메인 서열 분리 
        - Core Domain: 청약,심사 : 없어서는 안될 핵심 서비스이며, 연견 Up-time SLA 수준을 99.999% 목표, 배포주기는 보험가입의 경우 2주일 1회 미만
        - Supporting Domain: 가입이력 : 경쟁력을 내기위한 서비스이며, SLA 수준은 연간 60% 이상 uptime 목표, 배포주기는 각 팀의 자율이나 표준 스프린트 주기가 1주일 이므로 1주일 1회 이상을 기준으로 함.
        - General Domain: 결제 : 결제서비스로 3rd Party 외부 서비스를 사용하는 것이 경쟁력이 높음
        
### 폴리시의 이동과 컨텍스트 매핑 (점선은 Pub/Sub, 실선은 Req/Resp)
  ![image](https://user-images.githubusercontent.com/84304043/124390076-fd1c8d00-dd24-11eb-8368-849bf51d0874.png)

### 기능적/비기능적 요구사항을 커버하는지 검증

 ![image](https://user-images.githubusercontent.com/84304043/124390098-0e659980-dd25-11eb-9423-cc6a91604477.png)

  - (ok) 고객이 보험 상품을 선택하여 보험에 가입(청약)한다.
  - (ok) 청약 신청과 동시에 보험료 결제가 진행된다.
  - (ok) 보험료 결제가 완료되면 청약 신청이 완료된다.
  - (ok) 청약 신청이 완료되면 심사자가 배정된다.

![image](https://user-images.githubusercontent.com/84304043/124390116-1faea600-dd25-11eb-92b0-69225cff46dc.png)
    
  - (ok) 고객은 청약 신청을 취소할 수 있다. (단, 심사자 배정전까지만 가능. 심사Id로 구분)
    
![image](https://user-images.githubusercontent.com/84304043/124390127-2b9a6800-dd25-11eb-88e8-3ceedfe2b051.png)

 - (ok) 심사자가 승인하면 보험 계약이 체결된다. 
 - (ok) 심사자가 거절하면 보험료 결제가 취소된다.

 ![image](https://user-images.githubusercontent.com/84304043/124390145-394fed80-dd25-11eb-9c94-2c6fedc80715.png)

 - (ok) 고객은 보험 청약에 대한 정보 및 청약 진행 상태를 확인 할 수 있다.  


### 비기능 요구사항에 대한 검증

 ![image](https://user-images.githubusercontent.com/84304043/124390169-48cf3680-dd25-11eb-8f67-37114a119abf.png)

    - 마이크로 서비스를 넘나드는 시나리오에 대한 트랜잭션 처리
        - 고객 청약시 결제처리: 결제가 완료되지 않은 청약은 절대 받지 않는다고 결정하여, ACID 트랜잭션 적용. 청약 요청과 결제처리에 대해서는 Request-Response 방식 처리함.
        - 심사자 배정 완료시 청약완료처리: 심사에서 청약 마이크로서비스로 심사자 배정 완료 요청이 전달되는 과정에 있어서 청약 마이크로 서비스가 별도의 배포주기를 가지기 때문에 Eventual Consistency 방식으로 트랜잭션 처리함.
        - 나머지 모든 inter-microservice 트랜잭션: 취소처리, 심사승인, 심사거절 등 모든 이벤트에 대해 데이터 일관성의 시점이 크리티컬하지 않은 모든 경우가 대부분이라 판단, Eventual Consistency 를 기본으로 채택함.



## 헥사고날 아키텍처 다이어그램 도출
    
![image](https://user-images.githubusercontent.com/84304043/124096586-af3e2580-da95-11eb-88fe-4169ec0319df.png)

    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd subscription
mvn spring-boot:run

cd payment
mvn spring-boot:run 

cd underwriting
mvn spring-boot:run  

cd mypage
mvn spring-boot:run 
```

## CQRS

청약(subscription) 의 청약신청, 결제여부, 심사확인 등 Status 에 대하여 고객(Customer)이 조회 할 수 있도록 CQRS 로 구현하였다.
- subscription, payment, underwriting 개별 Aggregate Status 를 통합 조회하여 성능 Issue 를 사전에 예방할 수 있다.
- 비동기식으로 처리되어 발행된 이벤트 기반 Kafka 를 통해 수신/처리 되어 별도 Table 에 관리한다
- Table 모델링 (SubscriptionView)
 
  ![image](https://user-images.githubusercontent.com/84304043/124391036-8d5cd100-dd29-11eb-8135-3dae2fec424f.png)
 
- viewpage MSA ViewHandler 를 통해 구현 ("StorageRegistered" 이벤트 발생 시, Pub/Sub 기반으로 별도 Storageview 테이블에 저장)
  ![image](https://user-images.githubusercontent.com/84304043/124391093-ddd42e80-dd29-11eb-8fc1-cc671213c370.png)
  ![image](https://user-images.githubusercontent.com/84304043/124391162-43281f80-dd2a-11eb-92d0-a1baeb3fa557.png)

 
- 실제로 view 페이지를 조회해 보면 모든 storage에 대한 전반적인 예약 상태, 결제 상태, 리뷰 건수 등의 정보를 종합적으로 알 수 있다
```
   http GET http://localhost:8088/subsciptionViews
```
![image](https://user-images.githubusercontent.com/84304043/124577392-2c97da80-de88-11eb-91b5-ca1f87f0bba3.png)



## API 게이트웨이
      1. gateway 스프링부트 App을 추가 후 application.yaml내에 각 마이크로 서비스의 routes 를 추가하고 gateway 서버의 포트를 8080 으로 설정함


          - application.yml 예시
            ```
	spring:
	  profiles: docker
	  cloud:
	    gateway:
	      routes:
	        - id: subscription
	          uri: http://subscription:8080
	          predicates:
	            - Path=/subscriptions/** 
	        - id: payment
	          uri: http://payment:8080
	          predicates:
	            - Path=/payments/** 
	        - id: underwriting
	          uri: http://underwriting:8080
	          predicates:
	            - Path=/underwritings/** 
	        - id: mypage
	          uri: http://mypage:8080
	          predicates:
	            - Path= /subsciptionViews/**
	      globalcors:
	        corsConfigurations:
	          '[/**]':
	            allowedOrigins:
	              - "*"
	            allowedMethods:
	              - "*"
	            allowedHeaders:
	              - "*"
	            allowCredentials: true

	server:
	  port: 8080  
            ```

         
      2. Kubernetes용 Deployment.yaml 을 작성하고 Kubernetes에 Deploy를 생성함
          - Deployment.yaml 예시
          

            ```
	apiVersion: apps/v1
	kind: Deployment
	metadata:
	  name: gateway
	  namespace: insurancecontract
	  labels:
	    app: gateway
	spec:
	  replicas: 1
	  selector:
	    matchLabels:
	      app: gateway
	  template:
	    metadata:
	      labels:
		app: gateway
	    spec:
	      containers:
		- name: gateway
		  image: 740569282574.dkr.ecr.ap-northeast-1.amazonaws.com/user02-gateway:v1
		  ports:
		    - containerPort: 8080
            ```               
            

            ```
            Deploy 생성
            kubectl apply -f deployment.yaml
            ```     
          - Kubernetes에 생성된 Deploy. 확인
            
![image](https://user-images.githubusercontent.com/84304043/122843390-4d194e00-d33a-11eb-82b9-d156fce642d0.png)
	    
            
      3. Kubernetes용 Service.yaml을 작성하고 Kubernetes에 Service/LoadBalancer을 생성하여 Gateway 엔드포인트를 확인함. 
          - Service.yaml 예시
          
            ```
            apiVersion: v1
              kind: Service
              metadata:
                name: gateway
                namespace: storagerent
                labels:
                  app: gateway
              spec:
                ports:
                  - port: 8080
                    targetPort: 8080
                selector:
                  app: gateway
                type:
                  LoadBalancer           
            ```             

           
            ```
            Service 생성
            kubectl apply -f service.yaml            
            ```             
            
            
          - API Gateay 엔드포인트 확인
           
            ```
            Service  및 엔드포인트 확인 
            kubectl get svc -n storagerent           
            ```                

![image](https://user-images.githubusercontent.com/84304043/122770160-229aa700-d2e0-11eb-9f85-b6fcb8cabe0e.png)


# Correlation

창고대여 프로젝트에서는 PolicyHandler에서 처리 시 어떤 건에 대한 처리인지를 구별하기 위한 Correlation-key 구현을 
이벤트 클래스 안의 변수로 전달받아 서비스간 연관된 처리를 정확하게 구현하고 있습니다. 

아래의 구현 예제를 보면

청약(Subscription)을 하면 동시에 연관된 결제(Payment), 심사(Underwriting) 등의 서비스의 상태가 적당하게 변경이 되고,
심사 거절이 수행하면 다시 연관된 결제(Payment) 등의 서비스의 상태값 등의 데이터가 적당한 상태로 변경되는 것을
확인할 수 있습니다.

- 청약신청
```
http POST http://gateway:8080/subscriptions subscriptionStatus="created" productName="Fisrtcancer"
```  
![image](https://user-images.githubusercontent.com/84304043/124577809-94e6bc00-de88-11eb-8b08-79ca57ca7219.png)
- 청약신청 후 - 청약 상태
```
http GET http://gateway:8080/subscriptions
```  
![image](https://user-images.githubusercontent.com/84304043/124578172-eee78180-de88-11eb-87ea-f7e25502da79.png)

- 청약신청 후 - 결제 상태
```
http GET http://gateway:8080/payments
```  
![image](https://user-images.githubusercontent.com/84304043/124578498-3bcb5800-de89-11eb-9efc-64f1cd5f96f9.png)
- 청약신청 후 - 심사 상태
```
http GET http://gateway:8080/underwritings
```  
![image](https://user-images.githubusercontent.com/84304043/124578615-5271af00-de89-11eb-961a-c76891eee024.png)

- 심사에서 청약 거절
```
http PATCH http://localhost:8088/underwritings/1 underwritingStatus="refuseSubscription" subscriptionId=1
``` 
![image](https://user-images.githubusercontent.com/84304043/124578902-9a90d180-de89-11eb-8499-dca7d90648ca.png)
- 심사거절 후 - 심사 상태
```
http GET http://gateway:8080/underwritings
``` 
![image](https://user-images.githubusercontent.com/84304043/124579003-b4321900-de89-11eb-9a16-c5ec31742498.png)
- 심사거절 후 - 결제 상태
```
http GET http://gateway:8080/payments
``` 
![image](https://user-images.githubusercontent.com/84304043/124579299-ee9bb600-de89-11eb-858c-7348d7760e29.png)
- 심사거절 후 - 청약 상태
```
http GET http://gateway:8080/subscriptions
``` 
![image](https://user-images.githubusercontent.com/84304043/124579175-d7f55f00-de89-11eb-9f18-2b5710d89597.png)


## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다. (예시는 subscription 마이크로 서비스). 이때 가능한 현업에서 사용하는 언어 (유비쿼터스 랭귀지)를 그대로 사용하려고 노력했다. 현실에서 발생가능한 이벤트에 의하여 마이크로 서비스들이 상호 작용하기 좋은 모델링으로 구현을 하였다.

```
package insurancecontract;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Subscription_table")
public class Subscription {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long subscriptionId;
    private String subscriptionStatus;
    private Long paymentId;
    private Long underwritingId;
    private String productName;

    @PostPersist
    public void onPostPersist(){
        // SubscriptionCreated subscriptionCreated = new SubscriptionCreated();
        // BeanUtils.copyProperties(this, subscriptionCreated);
        // subscriptionCreated.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
        //----------------------------
        // PAYMENT 결제 진행 (POST방식)
        //----------------------------
        insurancecontract.external.Payment payment = new insurancecontract.external.Payment();

        payment.setSubscriptionId(this.getSubscriptionId());
        //payment.setProductName(this.getProductName());
        payment.setPaymentStatus("approvePayment");

        // mappings goes here
        SubscriptionApplication.applicationContext.getBean(insurancecontract.external.PaymentService.class)
            .approvePayment(payment);

        //----------------------------------
        // 이벤트 발행 --> SubscriptionCreated
        //----------------------------------
        SubscriptionCreated subscriptionCreated = new SubscriptionCreated();
        BeanUtils.copyProperties(this, subscriptionCreated);
        subscriptionCreated.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate(){
        
        if("confirmCancel".equals(this.getSubscriptionStatus())) {
            SubscriptionCancelled subscriptionCancelled = new SubscriptionCancelled();
            BeanUtils.copyProperties(this, subscriptionCancelled);
            subscriptionCancelled.publishAfterCommit();
        }

        if("reqCancel".equals(this.getSubscriptionStatus())) {
            CancelRequested cancelRequested = new CancelRequested();
            BeanUtils.copyProperties(this, cancelRequested);
            cancelRequested.publishAfterCommit();
        }

        
        if("confirmUnderwriterId".equals(this.getSubscriptionStatus())) {
            UnderwriterIdConfirmed underwriterIdConfirmed = new UnderwriterIdConfirmed();
            BeanUtils.copyProperties(this, underwriterIdConfirmed);
            underwriterIdConfirmed.publishAfterCommit();
        }

        
        if("confirmPaymentId".equals(this.getSubscriptionStatus())) {
            PaymentIdConfirmed paymentIdConfirmed = new PaymentIdConfirmed();
            BeanUtils.copyProperties(this, paymentIdConfirmed);
            paymentIdConfirmed.publishAfterCommit();
        }

        
        if("confirmRefuse".equals(this.getSubscriptionStatus())) {
            SubscriptionRefused subscriptionRefused = new SubscriptionRefused();
            BeanUtils.copyProperties(this, subscriptionRefused);
            subscriptionRefused.publishAfterCommit();
        }

    }

    @PrePersist
    public void onPrePersist(){
    }


    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    public Long getUnderwritingId() {
        return underwritingId;
    }

    public void setUnderwritingId(Long underwritingId) {
        this.underwritingId = underwritingId;
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}

```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package insurancecontract;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="subscriptions", path="subscriptions")
public interface SubscriptionRepository extends PagingAndSortingRepository<Subscription, Long>{

}
```
- 적용 후 REST API 의 테스트
```
# subscription 서비스의 청약신청
http POST http://gateway:8080/subscriptions subscriptionStatus="created" productName="Fisrtcancer"
  
# underwriting 서비스의 심사승인 요청
http PATCH http://gateway:8080//underwritings/1  underwritingStatus="approveSubscription"  subscriptionId=1

# payment 서비스의 결제 상태 확인
http GET http://gateway:8080/payments/1

```

## 동기식 호출(Sync) 과 Fallback 처리

분석 단계에서의 조건 중 하나로 청약 신청시 결제가 바로 진행되도록 청약(subscription) -> 결제(payment) 서비스는 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 청약, 결제 서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```
# PaymentService.java

package insurancecontract.external;

<import문 생략>

@FeignClient(name="payment", url="${prop.payment.url}")
public interface PaymentService {
    @RequestMapping(method= RequestMethod.GET, path="/payments")
    public void approvePayment(@RequestBody Payment payment);
}

```

- 청약신청 요청을 받은 직후(@PostPersist) 가능상태 확인 및 결제를 동기(Sync)로 요청하도록 처리
```
# Subscription.java (Entity)

    @PostPersist
    public void onPostPersist(){
    
        //----------------------------
        // PAYMENT 결제 진행 (POST방식)
        //----------------------------
        insurancecontract.external.Payment payment = new insurancecontract.external.Payment();

        payment.setSubscriptionId(this.getSubscriptionId());
        //payment.setProductName(this.getProductName());
        payment.setPaymentStatus("approvePayment");

        // mappings goes here
        SubscriptionApplication.applicationContext.getBean(insurancecontract.external.PaymentService.class)
            .approvePayment(payment);

        //----------------------------------
        // 이벤트 발행 --> SubscriptionCreated
        //----------------------------------
        SubscriptionCreated subscriptionCreated = new SubscriptionCreated();
        BeanUtils.copyProperties(this, subscriptionCreated);
        subscriptionCreated.publishAfterCommit();
        }
    }
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 결제 시스템이 장애가 나면 청약신청도 못받는다는 것을 확인:


```
# 결제 (payment) 서비스를 잠시 내려놓음 (ctrl+c)

# Payment 서비스 종료 후 청약신청
http POST http://gateway:8080/subscriptions subscriptionStatus="created" productName="Fisrtcancer"

# Payment 서비스 실행 후 청약신청
http POST http://gateway:8080/subscriptions subscriptionStatus="created" productName="Fisrtcancer"

# 청약신청 확인 
http GET http://gateway:8080/subscriptions/1  
```

- 또한 과도한 요청시에 서비스 장애가 도미노 처럼 벌어질 수 있다. (서킷브레이커, 폴백 처리는 운영단계에서 설명한다.)




## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트


결제가 이루어진 후에 숙소 시스템의 상태가 업데이트 되고, 예약 시스템의 상태가 업데이트 되며, 예약 및 취소 메시지가 전송되는 시스템과의 통신 행위는 비동기식으로 처리한다.
 
- 이를 위하여 결제가 승인되면 결제가 승인 되었다는 이벤트를 카프카로 송출한다. (Publish)
 
```
# Payment.java

package storagerent;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name="Payment_table")
public class Payment {

    ....

    @PostPersist
    public void onPostPersist(){
        ////////////////////////////
        // 결제 승인 된 경우
        ////////////////////////////

        // 이벤트 발행 -> PaymentApproved
        PaymentApproved paymentApproved = new PaymentApproved();
        BeanUtils.copyProperties(this, paymentApproved);
        paymentApproved.publishAfterCommit();
    }
    
    ....
}
```

- 예약 시스템에서는 결제 승인 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
# Reservation.java

package storagerent;

    @PostUpdate
    public void onPostUpdate(){
    
        ....

        if("reserved".equals(this.getReservationStatus())) {

            ////////////////////
            // 예약 확정된 경우
            ////////////////////

            // 이벤트 발생 --> ReservationConfirmed
            ReservationConfirmed reservationConfirmed = new ReservationConfirmed();
            BeanUtils.copyProperties(this, reservationConfirmed);
            reservationConfirmed.publishAfterCommit();
        }
        
        ....
        
    }

```

그 외 메시지 서비스는 예약/결제와 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, 메시지 서비스가 유지보수로 인해 잠시 내려간 상태 라도 예약을 받는데 문제가 없다.

```
# 메시지 서비스 (message) 를 잠시 내려놓음 (ctrl+c)

# 대여창고 등록
http POST http://localhost:8088/storages description="msg1" price=200000 storageStatus="available"

# Message 서비스 종료 후 창고대여
http POST localhost:8088/reservations storageId=5 price=200000 reservationStatus="reqReserve"   

# Message 서비스와 상관없이 창고대여 성공여부 확인
http GET http://localhost:8088/reservations/2

```
## 폴리그랏 퍼시스턴스 적용
```
Message Sevices : hsqldb사용
```
![image](https://user-images.githubusercontent.com/84304043/122845081-dda55d80-d33d-11eb-8d9f-a4e17735574e.png)
```
Message이외  Sevices : h2db사용
```
![image](https://user-images.githubusercontent.com/84304043/122845106-ed24a680-d33d-11eb-9124-aed5d9e7285b.png)

## Maven 빌드시스템 라이브러리 추가( pom.xml 설정변경 H2DB → HSQLDB) 
![image](https://user-images.githubusercontent.com/84304043/122845179-0fb6bf80-d33e-11eb-879a-1e6e8964ebb3.png)

# 운영


## CI/CD 설정

각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD는 buildspec.yml을 이용한 AWS codebuild를 사용하였습니다.

- CodeBuild 프로젝트를 생성하고 AWS_ACCOUNT_ID, KUBE_URL, KUBE_TOKEN 환경 변수 세팅을 한다
```
SA 생성
kubectl apply -f eks-admin-service-account.yml
```
![image](https://user-images.githubusercontent.com/84304043/122844500-c154f100-d33c-11eb-9ec0-5eb0fa3540d6.png)
```
Role 생성
kubectl apply -f eks-admin-cluster-role-binding.yml
```
![image](https://user-images.githubusercontent.com/84304043/122844538-d6ca1b00-d33c-11eb-818b-5a51404265c1.png)
```
Token 확인
kubectl -n kube-system get secret
kubectl -n kube-system describe secret $(kubectl -n kube-system get secret | grep eks-admin | awk '{print $1}')
```
![image](https://user-images.githubusercontent.com/86210580/122849832-34fbfb80-d347-11eb-9f6d-b1e379b3e1cf.png)

```
buildspec.yml 파일 
마이크로 서비스 storage의 yml 파일 이용하도록 세팅
```
![image](https://user-images.githubusercontent.com/84304043/122844673-201a6a80-d33d-11eb-8a52-a0fad02951d9.png)

- codebuild 실행
```
codebuild 프로젝트 및 빌드 이력
```
![image](https://user-images.githubusercontent.com/84304043/122846416-bdc36900-d340-11eb-9558-cad08d2615f2.png)
![image](https://user-images.githubusercontent.com/84304043/122861596-a2fdee00-d35a-11eb-9d73-7ff537c9e332.png)

- codebuild 빌드 내역 (Message 서비스 세부)

![image](https://user-images.githubusercontent.com/84304043/122846449-cd42b200-d340-11eb-8a33-aeff63915d61.png)

- codebuild 빌드 내역 (전체 이력 조회)

![image](https://user-images.githubusercontent.com/84304043/122846462-d5025680-d340-11eb-9914-b12b82a74ff5.png)



## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹: Hystrix 사용하여 구현함

시나리오는 예약(reservation)--> 창고(storage) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 예약 요청이 과도할 경우 CB 를 통하여 장애격리.

![image](https://user-images.githubusercontent.com/84304043/122866912-b6618700-d363-11eb-8247-dae264aa6fdf.png)


* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인: siege 실행 하였으나 storagerent 부하가 생성되지 않음.

```
kubectl run siege --image=apexacme/siege-nginx -n storagerent
kubectl exec -it siege -c siege -n storagerent -- /bin/bash
```
- Jmeter 로 부하 테스트 하였으나 실패건이 3%로 나오는것확인 -> 하지만 Jmeter 테스트와 연결해서 CB결과를 보여줘야할지 모르겠음.

![image](https://user-images.githubusercontent.com/84304043/122867174-10fae300-d364-11eb-8ab4-a2dbc6395f75.png)
![image](https://user-images.githubusercontent.com/84304043/122867281-31c33880-d364-11eb-9854-587ebade3a5b.png)


### 오토스케일 아웃
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 

- storage deployment.yml 파일에 resources 설정을 추가한다

![image](https://user-images.githubusercontent.com/84304043/122850814-d6378180-d348-11eb-9cd2-eb0873f1c8d7.png)

- storage 서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 50프로를 넘어서면 replica 를 10개까지 늘려준다. (X, 오류 발생)
```
kubectl autoscale deployment storage -n storagerent --cpu-percent=50 --min=1 --max=10
```

- 부하를 동시사용자 100명, 1분 동안 걸어준다.(X)
```
siege -c100 -t60S -v --content-type "application/json" 'http://storage:8080/storages POST {"desc": "BigStorage"}'
```
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다 (X)
```
kubectl get deploy storage -w -n storagerent 
```
- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다:(X)

- siege 의 로그를 보아도 전체적인 성공률이 높아진 것을 확인 할 수 있다. (X)


## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

```
kubectl delete destinationrules dr-storage -n storagerent
kubectl delete hpa storage -n storagerent
```

- seige 로 배포작업 직전에 워크로드를 모니터링 함(에러발생)
```
siege -c100 -t60S -r10 -v --content-type "application/json" 'http://storage:8080/storages POST {"desc": "BigStorage"}'
```

- 새버전으로의 배포 시작(X)
```
kubectl set image ...
```

- seige 의 화면으로 넘어가서 Availability 가 100% 미만으로 떨어졌는지 확인(X)

```
siege -c100 -t60S -r10 -v --content-type "application/json" 'http://storage:8080/storages POST {"desc": "BigStorage"}'
```

- 배포기간중 Availability 가 평소 100%에서 87% 대로 떨어지는 것을 확인. 원인은 쿠버네티스가 성급하게 새로 올려진 서비스를 READY 상태로 인식하여 서비스 유입을 진행한 것이기 때문. 이를 막기위해 Readiness Probe 를 설정함

```
# deployment.yaml 의 readiness probe 의 설정:
```

![image](https://user-images.githubusercontent.com/84304043/122858339-156bcf80-d355-11eb-9d1a-91da438ac905.png)


```
kubectl apply -f kubernetes/deployment.yml
```

- 동일한 시나리오로 재배포 한 후 Availability 확인(X)


# Self-healing (Liveness Probe)
- storage deployment.yml 파일 수정 
```
콘테이너 실행 후 /tmp/healthy 파일을 만들고 
90초 후 삭제
livenessProbe에 'cat /tmp/healthy'으로 검증하도록 함
```
![image](https://user-images.githubusercontent.com/84304043/122863309-80210900-d35d-11eb-8e07-8113c4ca6af9.png)

- kubectl describe pod storage -n storagerent 실행으로 확인(X)

