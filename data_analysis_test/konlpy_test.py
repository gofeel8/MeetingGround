from konlpy.tag import Okt
from collections import Counter
import json
import pandas as pd
import sys
from wordcloud import WordCloud
review_columns = (
    "id",  # 리뷰 고유번호
    "store",  # 음식점 고유번호
    "user",  # 유저 고유번호
    "score",  # 평점
    "content",  # 리뷰 내용
    "reg_time",  # 리뷰 등록 시간
)

def main():
#    nounlist=pd.read_pickle('./data/noun_list.pkl')
#    print(nounlist)
#    exit(1)
#    data=parse_review()
#    reviews=[]
#    #cnt=0   
#    for d in data:       
#        split_list=d.split(' ')
#        for el in split_list:
#            reviews.append(el)
                 
       
#    df = pd.DataFrame(reviews) 
#    pd.to_pickle(df, './data/split_list.pkl')
#    exit(1)
#   data=parse()
#    cnt=0
#    okt=Okt()
#   noun_list=[]   
#    for d in data:
#        cnt+=1
#        if cnt==10: break
#        noun=okt.nouns(d)
#        for i,v in enumerate(noun):
#            if len(v)<2:
#                noun.pop(i)
#        print(noun)
#        for v in noun:
#            noun_list.append(v)
      
#        if cnt==2:break
#    df = pd.DataFrame(noun_list) 
#    pd.to_pickle(df, './data/noun_list.pkl')
#    exit(1)
#    noun=pd.read_pickle('./data/noun_list.pkl')[0].tolist()
#   print(data)
#   exit(1)
   data=pd.read_pickle('./data/split_list.pkl')[0].tolist()
   count=Counter(data)
   noun_list=count.most_common(800)
   #print(noun)
   #print(noun_list)
   visualize(noun_list)

def visualize(noun_list):
    wc=WordCloud(font_path="C:\Windows\Fonts\malgunsl.ttf",\
    background_color="white",\
        width=1000,\
        height=1000,\
        max_words=100,\
        max_font_size=300)
    wc.generate_from_frequencies(dict(noun_list))
    wc.to_file('keyword_split_version.png')

def parse():
    try:
        with open('./data.json', encoding="utf-8") as f:
            data = json.loads(f.read())
    except FileNotFoundError:
        print(f"가 존재하지 않습니다.")
        exit(1)

     #stores = []  # 음식점 테이블
    reviews = []  # 리뷰 테이블
    categorys=[]
    for d in data:
        for c in d["category_list"]:
            categorys.append(c["category"])
            
        # for review in d["review_list"]:
        #     r = review["review_info"]
        #     #u = review["writer_info"]

        #     reviews.append(
        #          r["content"]
        #     )

     #store_frame = pd.DataFrame(data=stores, columns=store_columns)
     #review_frame = pd.DataFrame(data=reviews, columns=review_columns)
    return categorys

def parse_review():
    try:
        with open('./data.json', encoding="utf-8") as f:
            data = json.loads(f.read())
    except FileNotFoundError:
        print(f"가 존재하지 않습니다.")
        exit(1)

    #stores = []  # 음식점 테이블
    reviews = []  # 리뷰 테이블
    #categorys=[]
    for d in data:
        # for c in d["category_list"]:
        #     categorys.append(c["category"])
            
        for review in d["review_list"]:
            r = review["review_info"]
            #u = review["writer_info"]

            reviews.append(
                 r["content"]
            )

     #store_frame = pd.DataFrame(data=stores, columns=store_columns)
     #review_frame = pd.DataFrame(data=reviews, columns=review_columns)
    return reviews

if __name__ == '__main__':
    main()