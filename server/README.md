## typescript, type Orm, mysql 학습을 위한 boiler-plate

git pull로 server 디렉토리를 다운 받은 후
npm i 로
필요한 node_modules를 다운 받는다.

타입스크립트로 작성된 app.ts 파일로 서버를 실행하는 방법
npm run dev

mysql은 app-data-source.ts에서
개인 로컬 환경에 따라 다르기 때문에
본인이 직접 설정해주어야 올바르게 동작한다.
지난번에 설정한 app-data-source.ts를 유지하고
entities: ["src/entity/*.js"]로 되어있다면
entities: ["src/entity/*.*"]로 바꾸어야 npm run dev를 쳤을 때 동작한다.
