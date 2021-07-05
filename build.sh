build()
{
    cd $1
    mvn package -B -D maven.test.skip=true
    #docker build -t goang486/$1:$2 .
    #docker push goang486/$1:$2
    docker build -t 095661863019.dkr.ecr.ap-northeast-2.amazonaws.com/$1:$2 .
    docker push 095661863019.dkr.ecr.ap-northeast-2.amazonaws.com/$1:$2
    cd ..
}

ver=v1
if [ $# -gt 0 ]
then
    ver=$1
fi

for pkg in `echo gateway mypage payment subscription underwriting`
do
    build $pkg $ver
done
