if [ $# != 1 ] ; then 
	echo "no input files."
	exit 1;
fi

if [ ! -d "/data/data" ]; then
	export EXEC_PATH="./"
else
	export EXEC_PATH=/data/user/0/person.wangchen11.xqceditor/files/
fi

echo compile $1 
cat $1"/_base.txt" | grep mTitle
gcc -static -std=c99 -o $EXEC_PATH$1.elf $1/_answer.c

for i in 1 2 3 4 5 6 7 8 9 10
do
	$EXEC_PATH$1.elf < $1"/input$i.txt" > $1"/output"$i".txt"
	echo "input"$i" :"`cat $1"/input"$i".txt"`
	echo "output"$i":"`cat $1"/output"$i".txt"`
	echo 
done
rm -f $1.elf
echo ----------------------