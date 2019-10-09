
if [ ! -d "/data/data" ]; then
	export EXEC_PATH="./"
else
	export EXEC_PATH=/data/user/0/person.wangchen11.xqceditor/files/
fi

cp compile.sh $EXEC_PATH"compile.sh"
chmod 777 $EXEC_PATH"compile.sh"

for dir in ./[0-9]*.[0-9]*
do
	#echo $dir
	$EXEC_PATH"compile.sh" $dir
done
