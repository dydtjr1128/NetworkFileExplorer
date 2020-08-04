# 노드 셋팅
FROM node:13.12.0-alpine

# 리눅스의 /app을 저장공간으로 사용하겠다는 의미. cd frontend와 동일
WORKDIR /frontend

# add `/app/node_modules/.bin` to $PATH
ENV PATH /frontend/node_modules/.bin:$PATH

# install app dependencies
COPY jsconfig.json ./
COPY package.json ./
COPY package-lock.json ./
RUN npm install --silent
RUN npm install react-scripts@3.4.1 -g --silent

# start app
ENTRYPOINT ["npm", "start"]