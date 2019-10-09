#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <time.h>
#include <pthread.h>
#include "List/LinkedList.h"

typedef struct {
	LinkedList *mExecFiles;
	pthread_mutex_t mMutexLock;
	int mThreadNumber;
	int mDoneThreadNumber;
	int mError;
	pthread_t *mThreads;
} MutExec;

static LinkedList *getCmdsFromFile(const char *fileName){
	LinkedList * cmds = newLinkedList();
	
	FILE *file = fopen(fileName,"r");
	if(file==NULL){
		fprintf(stderr,"can not open file:%s\n",fileName);
		return cmds;
	}
	char lineBuffer[1024*64];
	while(lineBuffer == fgets(lineBuffer,sizeof(lineBuffer),file)){
		int len = strlen(lineBuffer);
		if( (len>0) && (lineBuffer[len-1]=='\n') ){
			lineBuffer[len-1] = 0;
		}
		//printf("read:%s\n",lineBuffer);
		LinkedList_addLast(cmds,strdup(lineBuffer));
	}
	
	return cmds;
}

char *nextCmd(MutExec *mutExec){
	char *data = NULL;
	pthread_mutex_lock(&(mutExec->mMutexLock));
	while(!LinkedList_isEmpty(mutExec->mExecFiles)){
		LinkedList *cmds = (LinkedList *) LinkedList_removeAt(mutExec->mExecFiles,0);
		while(!LinkedList_isEmpty(cmds)){
			data = (char *)LinkedList_removeAt(cmds,0);
			if(data!=NULL)
				break;
		}
		if(!LinkedList_isEmpty(cmds))
			LinkedList_addAt(mutExec->mExecFiles,cmds,0);
		if(data!=NULL)
			break;
	}
	pthread_mutex_unlock(&(mutExec->mMutexLock));
	return data;
}

void *runTaskThread(void *data){
	MutExec *mutExec = (MutExec *)data;
	while(0==mutExec->mError){
		char *cmd = nextCmd(mutExec);
		if(cmd==NULL)
			break;
		int ret = system(cmd);
		if(ret!=0){
			mutExec->mError = ret;
		}
			
		free(cmd);
	}
	return NULL;
}

int main(int argc,char **argv)
{
	/*
	argc = 3;
	char *prams[] = {"file.exe","1","../run.sh",NULL};
	argv = prams;
	*/
	time_t startTime = time(NULL);
	
	int i = 0; 
	/*
	for(i=0;i<argc;i++)
		printf("%s\n",argv[i]);*/
	if(argc<3){
		fprintf(stderr,"too few args.\n");
		return -1;
	}
	
	int threadNumber = atoi(argv[1]);
	if(threadNumber<1)
		threadNumber = 1;
	if(threadNumber>32)
		threadNumber = 32;
		
	MutExec _mutExec;
	MutExec *mutExec = &_mutExec;
	printf("use thread number:%d\n",threadNumber);
	
	mutExec->mExecFiles = newLinkedList();
	pthread_mutex_init( &(mutExec->mMutexLock) , NULL );
	mutExec->mThreadNumber = threadNumber;
	mutExec->mDoneThreadNumber = 0;
	mutExec->mError = 0;
	mutExec->mThreads = (pthread_t*)malloc(sizeof(pthread_t) * mutExec->mThreadNumber);
	
	
	for(i=2;i<argc;i++){
		LinkedList *cmds = getCmdsFromFile(argv[i]);
		LinkedList_addLast(mutExec->mExecFiles,cmds);
	}
	
	for( i = 0; i < mutExec->mThreadNumber; i++)
    {
        //printf("Creating thread %d\n", i);
        int rc = pthread_create(&(mutExec->mThreads[i]), NULL, runTaskThread, mutExec );
        if (rc)
        {
            printf("ERROR;pthread_create return code is %d\n", rc);
        }
    }
	
    for( i = 0; i < mutExec->mThreadNumber; i++) {
        pthread_join((mutExec->mThreads[i]), NULL);
	}
	
	free(mutExec->mThreads);
	
	while(!LinkedList_isEmpty(mutExec->mExecFiles)){
		LinkedList *cmds = (LinkedList *) LinkedList_removeAt(mutExec->mExecFiles,0);
		while(!LinkedList_isEmpty(cmds)){
			char *data = (char *)LinkedList_removeAt(cmds,0);
			//printf("remove:%s\n",data);
			if(data!=NULL)
				free(data);
		}
	}
	deleteLinkedList(mutExec->mExecFiles);
	pthread_mutex_destroy(&(mutExec->mMutexLock));
	
	time_t endTime = time(NULL);
	int timeUsed = endTime - startTime;
	int hour = (int)((timeUsed/60)/60);
	int min = (int)((timeUsed/60)%60);
	int sec = (int)(timeUsed%60);
	printf("time used:[%02d:%02d:%02d]\n",hour,min,sec);
	return mutExec->mError;
}
