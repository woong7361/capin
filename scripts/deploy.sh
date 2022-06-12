# 가동중인 awsstudy 도커 중단 및 삭제
sudo docker ps -a -q --filter "name=finalp" | grep -q . && docker stop finalp && docker rm finalp | true
#sudo docker ps -a -q --filter "name=nginx" | grep -q . && docker stop nginx && docker rm nginx | true
#이건 엔진엑스있을때 주석 없애기

# 기존 이미지 삭제
#sudo docker rmi woong7361/finalp:1.0
sudo docker rmi chrysan5/finalp:1.0

# 도커허브 이미지 pull
#sudo docker pull woong7361/finalp:1.0
sudo docker pull chrysan5/finalp:1.0

# 도커 run
docker run -d -p 8080:8080 -v /home/ec2-user:/config --name finalp chrysan5/finalp:1.0
#이거 나중에 엔진엑스하면 주석처리됨


# docker-compose run
#docker-compose -f /home/ubuntu/docker-compose.yml up -d
#이것도 엔진엑스 있을경우 주석 없애야됨

# 사용하지 않는 불필요한 이미지 삭제 -> 현재 컨테이너가 물고 있는 이미지는 삭제되지 않습니다.
docker rmi -f $(docker images -f "dangling=true" -q) || true

