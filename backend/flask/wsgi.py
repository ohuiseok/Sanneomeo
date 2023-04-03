from flask import Flask, request
from DBInfo import DBInfo
import pandas as pd
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity, euclidean_distances
from sklearn.preprocessing import MinMaxScaler


app = Flask(__name__)

# MySQL 데이터베이스 연결 설정
mydb = DBInfo.mydb()


# Flask API
@app.route('/recommendCourse/<int:course_seq>', methods=['GET'])
def recommend_course(course_seq):
    curs = mydb.cursor()
    sql = "select * from tbl_course"
    curs.execute(sql)
    result = curs.fetchall()
    mydb.close()

    df = pd.DataFrame(result)
    df.columns = ['course_seq', 'mountain_seq', 'name', 'introduction', 'length', 'time', 'difficulty_mean',
                  'review_cnt', 'review_mean', 'slope_mean', 'altitude', 'recommend', 'best_trail']

    features = ['length', 'time', 'altitude']

    # 정규화
    scaler = MinMaxScaler()
    df[features] = scaler.fit_transform(df[features])

    X = df[features].values


    target_trail = df[df['course_seq'] == course_seq][features].to_dict(orient='records')[0]
    target_trail['course_seq'] = course_seq;  # 북악산 나오는지 보려고 이름암거나씀

    y = np.array([[target_trail[feature] for feature in features]])

    distances = euclidean_distances(X, y)

    ranked_trails = sorted(zip(df['course_seq'], distances), key=lambda x: x[1])
    recommended_trails = [trail[0] for trail in ranked_trails if trail[0] != target_trail['course_seq']][:10]

    recommended_result = {"recommended_result" : recommended_trails}

    return recommended_result

@app.route('/targetCourse', methods=['GET'])
def target_course():
    data = request.args

    # 이거로 유사도 뽑고
    difficulty = data.get('level')
    location = data.get('region')
    purpose = data.get('purpose')
    time = data.get('time')


    # 입력으로 들어온 문제 정보
    difficulty = 1
    location = '서울'
    purpose = 1
    time = 30

    # 코스 정보 가져오기
    curs = mydb.cursor()
    sql = "select c.course_seq , c.mountain_seq, c.difficulty_mean, c.time, m.si from tbl_course c left join tbl_mountain m on c.mountain_seq = m.mountain_seq"
    curs.execute(sql)
    result = curs.fetchall()
    mydb.close()

    # 데이터프레임 생성 및 필터링
    df = pd.DataFrame(result)
    df.columns = ['course_seq', 'mountain_seq', 'difficulty_mean', 'time', 'si']


    # 난이도 필터링
    if difficulty == 1 : #쉬움
        df = df[df['difficulty_mean'] <= 1.0]
    elif difficulty == 2: #중간
        df = df[(df['difficulty_mean'] > 1.0) & (df['difficulty_mean'] < 1.3)]
    elif difficulty == 3: #어려움
        df = df[(df['difficulty_mean'] >= 1.3) ]

    print(df)

    # 지역 필터링
    df = df[df['si'].str.contains(location)]
    print(df)

    # 시간 필터링
    if time == 1: # 30분 미만
        df = df[df['time'] < 30]
    elif time == 2: # 30분 이상 1시간 미만
        df = df[(df['time'] >= 30 ) & (df['time'] < 60)]
    elif time == 3: # 1시간 이상 2시간 미만
        df = df[(df['time'] >= 60) & (df['time'] < 120)]
    elif time == 4: # 2시간 이상 3시간 미만
        df = df[(df['time'] >= 120) & (df['time'] < 180)]
    elif time == 5: # 3시간 이상
        df = df[(df['time'] >= 180) ]

    # 목적 필터링
    # df['sum'] = df['difficulty_mean'] + df['time']
    # if purpose == 1 : #힐링
    #     df = df.loc[df['sum'].idxmin(), ['difficulty_mean', 'time']]
    # elif purpose == 2 : #도전
    #     df['sum'] = df['difficulty_mean'] + df['time']
    #     df = df.loc[df['sum'].idxmax()]

    print("목적 필터링")
    print(df)



    return "안녕"

if __name__ == '__main__':
    app.run(debug=True)