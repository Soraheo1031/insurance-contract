build()
{
    cd $1
    mvn package -B -D maven.test.skip=true
    #docker build -t soraHeo/$1:$2 .
    #docker push soraHeo/$1:$2
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