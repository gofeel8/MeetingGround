from pymongo import MongoClient,GEO2D,GEOSPHERE
from pymongo.cursor import CursorType
from bson.son import SON
import json
import pprint
host="localhost"
port="27017"
mongo=MongoClient(host,int(port))
print(mongo)
db=mongo.restaurant
def main():
    #db.data.create_index([("loc", GEO2D)])//geo 인덱스 생성
    #insertAll()// json 데이터 전부 넣어줌, 
    query = {"loc": SON([("$near", [127.341441, 36.353687]), ("$maxDistance", 1000)])}
    for doc in db.data.find(query).limit(10):
      pprint.pprint(doc)
    #print(insert_item_one(mongo,{"test":"Hello Mongo"},"restaurant","data"))
    
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