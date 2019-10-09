#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <sys/stat.h>
#include <fcntl.h>

#define BUFFER_SIZE 1024*32 
/*
这是一个简单的helloworld程序
这个程序只做一件事:输出hello world
by 望尘11
*/

int writeFile(int fd,char *filePath);

int main(int argc,char **argv)
{
	/*
	argc=3;
	char *a[]=
	{
		"",
		"mycp.c",
		"mycp.bbbbbbbb.c"
	};
	argv=a;
	*/
	if(argc==3)
	{
		int fd=open(argv[2],O_WRONLY|O_CREAT);
		if(fd<=0)
		{
			printf("打开文件失败:%s\n",argv[2]);
			return 0;
		}
		else
		{
			if(writeFile(fd,argv[1])==-1)
			{
				printf("写入文件失败:%s\n",argv[1]);
			}
			close(fd);
		}
	}
	else
	{
		printf("cp need 2 param!\n");
	}
	return 0;
}


int writeFile(int fd,char *filePath)
{
	//printf("write file:%s\n",filePath);
	int fileHandler=open(filePath,O_RDONLY);
	if(fileHandler<=0)
		return -1;
	char *buffer=(char *)malloc(BUFFER_SIZE+1);
	if(buffer==NULL)
		printf("malloc return NULL!");
	int readLen=0;
	while( 0<(readLen=read(fileHandler,buffer,BUFFER_SIZE)) )
	{
		write(fd,buffer,readLen);
	}
	
	free(buffer);
	close(fileHandler);
	return 0;
}

