import json
from collections import Counter
import os
def main():
  
    data_category=parse_category()
    count=Counter(data_category)
    category_list=count.most_common(1000)
    print(category_list)    
    # count_category(count)
    # data=parse()
    # reviews=[]
    # keyword= "가성비"
    # for d in data:
    #    if (keyword in d):
    #        reviews.append(d)
    # for r in reviews:
    #     print(r)
    #     print("----------------------------")

def count_category(counter):
    if not os.path.isfile("./category.txt"):       
       f = open("counter.txt","w+",encoding="utf8")
       cnt=0
       for c in counter:
        cnt+=1           
        f.write(str(cnt)+". "+str(c)+"\n")
       f.close()       
    else:
        f = open("./category.txt","r+")
        counter = int(f.readline())
        counter += 1
        f.seek(0)
        f.write(str(counter))
        f.close()
        print (counter)

def parse():
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

def parse_category():
    try:
        with open('./data.json', encoding="utf-8") as f:
            data = json.loads(f.read())
    except FileNotFoundError:
        print(f"가 존재하지 않습니다.")
        exit(1)

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
    
    return categorys

if __name__ == '__main__':
    main()