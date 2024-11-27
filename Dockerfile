FROM stablebaselines/rl-baselines3-zoo-cpu

USER root

RUN apt update && apt install -y openjdk-11-jdk

COPY ./source /source

RUN mkfifo /source/j2p /source/p2j

RUN chmod +x /source/resttestgen-framework/gradlew
RUN cd /source/resttestgen-framework/ && ./gradlew build --no-daemon -x test

CMD sh /source/scripts/run.sh