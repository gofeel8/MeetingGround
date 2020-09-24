from pymongo import MongoClient,GEO2D,GEOSPHERE
from pymongo.cursor import CursorType
from bson.son import SON
import json
import pprint
import pandas as pd
import numpy as np
from random import shuffle
from sklearn.metrics.pairwise import cosine_similarity
from operator import itemgetter
host="localhost"
port="27017"
mongo=MongoClient(host,int(port))
print(mongo)
db=mongo.restaurant

def main():
    #db.data.create_index([("loc", GEO2D)])
    query = {"loc": SON([("$near", [127.341441, 36.353687]), ("$maxDistance", 1000)])}
    result=db.data.find(query).limit(1000)
    res_list=[]    
    for el in result:
         res_list.append(el)
    simil_list=[]
    tag_col=[[0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0]]
    idx=0    
    for el in res_list:
           aa=[]
           aa.append(el['tags'])           
           simil=cosine_similarity(tag_col,aa)
           simil_list.append({"id":idx,"simil":simil[0][0]})
           idx+=1
    sorted_list=sorted(simil_list,reverse=True,key=itemgetter('simil'))
    for el in sorted_list:
        pprint.pprint(str(el['id'])+" "+str(el['simil']))
    for i in range(0,10):
        print(res_list[sorted_list[i]['id']])
                   
    # for doc in db.data.find(query):
    #   pprint.pprint(doc)
    # try:
    #     with open('./data.json', encoding="utf-8") as f:
    #         data = json.loads(f.read())
    # except FileNotFoundError:
    #     print(f"가 존재하지 않습니다.")
    #     exit(1)
    # try:
    #     with open('./keyword.json', encoding="utf-8-sig") as f1:
    #         tags = json.loads(f1.read())
    # except FileNotFoundError:
    #     print(f"가 존재하지 않습니다.")
    #     exit(1)
    # #cnt=0  
    # for d in data:
    #     # if cnt>=1:break
    #     # cnt+=1
    #     # print(d)
    #     # print(float(d["latitude"]))
    #     try:
    #         mongo_geojson={"loc": [float(d["longitude"]), float(d["latitude"])]}
    #         d.update(mongo_geojson)       
    #         #insert_item_one(mongo,d,"restaurant","data")
    #     except TypeError:continue
    #     categorys=[]
    #     reviews=[]
    #     tag_col=[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]       
    #     for c in d["category_list"]:
    #         categorys.append(c["category"])
        
    #     for review in d["review_list"]:
    #         r = review["review_info"]            
    #         reviews.append(r["content"])
    #     if len(reviews)>0:
    #         for r in reviews:
    #             idx=0
    #             for t in tags:
    #                 if idx>=7: break      
    #                 for el in tags[t]:
    #                     if (el in r):
    #                         tag_col[idx]=1
    #                         break 
    #                 idx+=1
                        
    #     if len(categorys)>0:           
    #         for c in categorys:
    #             idx=0
    #             for t in tags:
    #                 if idx<=6: 
    #                     idx+=1 
    #                     continue      
    #                 for el in tags[t]:
    #                     if (el in c):
    #                         tag_col[idx]=1
    #                         break 
    #                 idx+=1
    #     try:
    #         mongo_geojson={"tags":tag_col}
    #         d.update(mongo_geojson)       
    #         insert_item_one(mongo,d,"restaurant","data")
    #     except TypeError:continue

def insert_item_one(mongo, data, db_name=None, collection_name=None):
    result = mongo[db_name][collection_name].insert_one(data).inserted_id
    return result

def insertAll():
    try:
        with open('./data.json', encoding="utf-8") as f:
            data = json.loads(f.read())
    except FileNotFoundError:
        print(f"가 존재하지 않습니다.")
        exit(1)
    #cnt=0
    for d in data:
        #if cnt>=1:break
        #cnt+=1
        # print(d)
        #print(float(d["latitude"]))
        try:
            mongo_geojson={"loc": [float(d["longitude"]), float(d["latitude"])]}
            d.update(mongo_geojson)       
            insert_item_one(mongo,d,"restaurant","data")
        except TypeError:continue

if __name__ == '__main__':
    main()