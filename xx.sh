rm yaml/alldeploy.yml
for a in `find ./ -name "*ml" | grep kubernetes`
do     
     cat $a | tee -a yaml/alldeploy.yml
     echo | tee -a yaml/alldeploy.yml
     echo --- | tee -a yaml/alldeploy.yml 
done
