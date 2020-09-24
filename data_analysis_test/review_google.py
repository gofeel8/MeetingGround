
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
from collections import OrderedDict
from time import sleep
import pandas as pd
import json
DATA_DIR = './data/data.pkl'
options = webdriver.ChromeOptions()
options.add_argument('headless')    # 웹 브라우저를 띄우지 않는 headless chrome 옵션 적용
options.add_argument('disable-gpu')    # GPU 사용 안함
options.add_argument('window-size=1920x1080')
options.add_argument('ignore-certificate-errors')
options.add_argument("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")

options.add_argument('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/81.0.4044.122 Safari/537.36')
driver = webdriver.Chrome('./chromedriver', options=options) #  옵션 적용
# driver = webdriver.Chrome('./chromedriver')
file_data = OrderedDict()
json_data = []
# 식당 검색 + 해당 식당 리뷰페이지 들어가기
def main():
    global json_data, file_data

    # file_data = OrderedDict()
    driver.implicitly_wait(4)
    # driver.get("https://www.google.co.kr/maps")

    # query = '서울특별시 용산구 동자동 43-205 버거킹'
    # query = '서울특별시 광진구 화양동 47-5 와플대학'
    # query = 'asdf'
    #parse()
    data = pd.read_pickle('./data/data_list.pkl').values.tolist()
    q_list = []
    for d in data:
        q_list.append(d[0])  
    cnt=0
    cnt1=0
    for query in q_list:
        file_data = OrderedDict()
        driver.get("https://www.google.co.kr/maps")
        search = driver.find_element_by_xpath("//input[@id='searchboxinput']")
        search.send_keys(query)
        search.send_keys(Keys.ENTER)
        sleep(1)
        cnt+=1
        cnt1+=1
        print(cnt)
        if(cnt>250000):exit(1)
        # 검색했는데 장소가 여러 개일 때
        try:
            # print('Element!!')
            file_data['name'] = query
            driver.find_element_by_xpath("//div[@class='section-result']").click()
            sleep(1)
            try:
                image_container = driver.find_element_by_css_selector('.section-hero-header-image-hero-container')
                image = image_container.find_element_by_css_selector('img').get_attribute('src')
                file_data['image'] = image
                driver.find_element_by_xpath("//button[@class='jqnFjrOWMVU__button gm2-caption']").click()
                sleep(1)
                review_crawling()
                json_data.append(file_data)

            except (NoSuchElementException):
                print('Review not found')                
        # 검색했는데 장소가 1 개 또는 0 개 일 때
        except (NoSuchElementException):
            print('NoElement!!')
            # 장소가 1개 일 때
            try:
                image_container = driver.find_element_by_css_selector('.section-hero-header-image-hero-container')
                image = image_container.find_element_by_css_selector('img').get_attribute('src')
                file_data['image'] = image               
                driver.find_element_by_xpath("//button[@class='jqnFjrOWMVU__button gm2-caption']").click()
                sleep(1)
                review_crawling()
                json_data.append(file_data)

            # 장소가 존재하기 않을 때
            except (NoSuchElementException):
                print('Place not found')
            
        finally:
            if cnt1==1000:
                df = pd.DataFrame(json_data)
                print("저장완료")
                pd.to_pickle(df, DATA_DIR)
                cnt1=0

    sleep(2)
    print('finish')
    driver.close()

# 리뷰 크롤링 하기
def review_crawling():
    global json_data, file_data
    driver.find_element_by_xpath("//div[@class='cYrDcjyGO77__dropdown-icon']").click()
    sleep(1)
    webdriver.ActionChains(driver).send_keys(Keys.ESCAPE).perform()
    sleep(1)
    scroll_num = 0
    while scroll_num < 5:
        webdriver.ActionChains(driver).send_keys(Keys.END).perform()
        scroll_num += 1
        sleep(1)

    html = driver.page_source
    soup = BeautifulSoup(html, 'html.parser')
    review_list = soup.select('.section-layout')
    file_data['reviews'] = []
    idx = 1

    for review in review_list[4]:
        content = review.select('.section-review-review-content > .section-review-text')
        if content and content[0].text:
            input_data = { 'id': idx, 'content': content[0].text }
            file_data['reviews'].append(input_data)
            idx += 1
            # print('---------')
def parse():
    try:
       with open('./data.json', encoding="utf-8") as f:
            data = json.loads(f.read())
    except FileNotFoundError:
        print("가 존재하지 않습니다.")
        exit(1)
    json_list=[]
    for d in data:
       address=(d["address"] if d["address"]!=None else "")
       query=address+" "+d["name"]
       json_list.append({'query':query})
    df = pd.DataFrame(json_list)
    print(df)
    pd.to_pickle(df, './data/data_list.pkl')

if __name__ == '__main__':
    main()