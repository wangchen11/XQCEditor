#include "LinkedList.h"
#include <stdio.h>
#include <stdlib.h>

#define KLOGE printf
#define LOG_TAG "LinkedList"
struct LinkedListNode_St {
    const  void  *data;
    struct LinkedListNode_St *pro;
    struct LinkedListNode_St *next;
};

struct LinkedList_St {
    int            size;
    int            cachePos;
    LinkedListNode *cacheNode;
    LinkedListNode *head;
};

static LinkedListNode *getNextNNode(LinkedListNode *now,int n)
{
    if(n>0)
    {
        while(n>0){
            now = now->next;
            n--;
            if(now == NULL)
                return NULL;
        }
    }
    else
    {
        while(n<0){
            now = now->pro;
            n++;
            if(now == NULL)
                return NULL;
        }
    }
    return now;
}

static LinkedListNode *LinkedList_getNode(LinkedList* thiz,int pos)
{
    LinkedListNode *retNode = NULL;
    
    if( thiz->cacheNode != NULL )
    {
        if(pos>= thiz->cachePos ){
            retNode = getNextNNode( thiz->cacheNode,pos - thiz->cachePos );
        }
        else 
        if( thiz->cachePos - pos <= pos )
        {
            retNode = getNextNNode( thiz->cacheNode,pos - thiz->cachePos );
        }
        else 
        {
            retNode = getNextNNode( thiz->head,pos );
        }
    }
    else
    {
        retNode = getNextNNode( thiz->head,pos );
    }
    thiz->cacheNode = retNode;
    thiz->cachePos  = pos;
    
    return retNode;
}

LinkedList* newLinkedList()
{
    LinkedList* thiz = (LinkedList*)malloc( sizeof(LinkedList) );
    if(thiz==NULL)
    {
        KLOGE("out of memory!");
        return NULL;
    }
    thiz->size      = 0;
    thiz->cachePos  = 0;
    thiz->cacheNode = NULL;
    thiz->head      = NULL;
    return thiz;
}

static LinkedListNode* newLinkedListNode(const void *data)
{
    LinkedListNode* thiz = (LinkedListNode*)malloc( sizeof(LinkedListNode) );
    if(thiz==NULL)
    {
        KLOGE("out of memory!");
        return NULL;
    }
    thiz->data = data;
    thiz->pro  = NULL;
    thiz->next = NULL;
    return thiz;
}

LinkedList* LinkedList_clone(LinkedList* thiz)
{
    LinkedList* list = newLinkedList();
    LinkedList_addAll(list,thiz);
    return list;
}

void deleteLinkedList(LinkedList* thiz)
{
    LinkedList_clean(thiz);
    free(thiz);
}

BOOL LinkedList_isEmpty(LinkedList* thiz)
{
    return LinkedList_size(thiz) == 0;
}

int  LinkedList_size(LinkedList* thiz)
{
    return thiz->size;
}

void LinkedList_clean(LinkedList* thiz)
{
    while( !LinkedList_isEmpty(thiz) )
    {
        LinkedList_removeAt(thiz,0);
    }
}

BOOL LinkedList_addAll(LinkedList* thiz,LinkedList* other)
{
    int i=0;
    for(i=0;i<LinkedList_size(other);i++){
        LinkedList_addLast(thiz,LinkedList_get(other,i) );
    }
    return FALSE;
}

BOOL LinkedList_addAt(LinkedList* thiz,const void *data,int pos)
{
    // TODO 
    LinkedListNode *newNode = newLinkedListNode(data);
    if( LinkedList_isEmpty(thiz) )
    {
        thiz->head = newNode;
        thiz->size++;
        return TRUE;
    }
    
    LinkedListNode *pro = LinkedList_getNode(thiz,pos-1);
    if(pro == NULL)
    {
        KLOGE("pos out of bouds!size:%d,pos:%d",LinkedList_size(thiz),pos-1);
        free(newNode);
        return FALSE;
    }
    
    if(thiz->cacheNode!=NULL)
    {
        if(thiz->cachePos>=pos)
            thiz->cachePos++;
    }
    
    LinkedListNode *next = pro->next;
    
    pro->next = newNode;
    newNode->pro = pro;
    newNode->next = next;
    if(next!=NULL)
        next->pro=newNode;
    thiz->size++;
    return TRUE;
}

BOOL LinkedList_addLast (LinkedList* thiz,const void *data)
{
    return LinkedList_addAt(thiz,data, LinkedList_size(thiz) );
}

BOOL LinkedList_addFirst(LinkedList* thiz,const void *data)
{
    return LinkedList_addAt(thiz,data, 0 );
}

BOOL LinkedList_remove(LinkedList* thiz,const void *data,compare_fp cmp)
{
    int index = LinkedList_indexOf(thiz,data,cmp);
    if(index<0)
        return FALSE;
    LinkedList_removeAt(thiz,index);
    return TRUE;
}

void *LinkedList_removeAt(LinkedList* thiz,int pos)
{
    LinkedListNode *node = LinkedList_getNode(thiz,pos);
    if(node == NULL)
    {
        KLOGE("pos out of bouds!size:%d,pos:%d",LinkedList_size(thiz),pos);
        return NULL;
    }
    if(thiz->cacheNode!=NULL)
    {
        if(pos > thiz->cachePos){
            thiz->cachePos --;
        }	
        else 
        if(pos == thiz->cachePos)
        {
            thiz->cachePos--;
            thiz->cacheNode = thiz->cacheNode->pro;
        }else{} // do nothing
    }
    
    
    LinkedListNode *pro  = node->pro;
    LinkedListNode *next = node->next;
    
    if(pro!=NULL)
        pro->next = next;
    if(next!=NULL)
        next->pro = pro;
    void *data = (void *)(node->data);
    node->pro  = NULL;
    node->next = NULL;
    free(node);
    thiz->size--;
    if(thiz->head==node)
        thiz->head=next;
    return data;
}

void *LinkedList_get(LinkedList* thiz,int pos)
{
    LinkedListNode *node = LinkedList_getNode(thiz,pos);
    if(node == NULL)
    {
        KLOGE("pos out of bouds!size:%d,pos:%d",LinkedList_size(thiz),pos);
        return NULL;
    }
    return (void*)(node->data);
}

BOOL LinkedList_push(LinkedList* thiz,const void *data)
{
    return LinkedList_addLast(thiz,data);
}

void *LinkedList_pop(LinkedList* thiz)
{
    return LinkedList_removeAt(thiz,LinkedList_size(thiz)-1 );
}

void *LinkedList_peek(LinkedList* thiz)
{
	return LinkedList_get(thiz,LinkedList_size(thiz)-1 );
}

BOOL LinkedList_enqueue(LinkedList* thiz,const void *data)
{
    return LinkedList_addFirst(thiz,data);
}

void *LinkedList_dequeue(LinkedList* thiz)
{
    return LinkedList_removeAt(thiz,0);
}


void **LinkedList_toArray(LinkedList* thiz)
{
	int i;
	int len = LinkedList_size(thiz);
	void **array = (void**)malloc( sizeof(void*)*(len+1) );
	array[len]=NULL;
	for(i=0;i<len;i++)
	{
		array[i] = LinkedList_get(thiz,i);
	}
	return array;
}

int  LinkedList_indexOf(LinkedList* thiz,const void *data,compare_fp cmp)
{
    // TODO 
    if(cmp==NULL)
    {
        int i=0;
        for(i=0;i<LinkedList_size(thiz);i++)
        {
            LinkedListNode* node = LinkedList_getNode(thiz,i);
            if(node==NULL)
            {
                KLOGE("LinkedList_indexOf error!");
            }
            if(node->data==data)
                return i;
        }
    }
    else
    {
        int i=0;
        for(i=0;i<LinkedList_size(thiz);i++)
        {
            LinkedListNode* node = LinkedList_getNode(thiz,i);
            if(node==NULL)
            {
                KLOGE("LinkedList_indexOf error!");
            }
            if( cmp(node->data,data)==0 )
                return i;
        }
    }
    return 0;
}

void LinkedList_sort(LinkedList* thiz,compare_fp cmp)
{
    int i,j;
    for(i=LinkedList_size(thiz)-1;i>0;i--)
    {
        for(j=0;j<i;j++)
        {
            LinkedListNode *nodej = LinkedList_getNode(thiz,j);
            LinkedListNode *nodej1 = LinkedList_getNode(thiz,j+1);
            if( cmp(nodej->data,nodej1->data) > 0 )
            {
                const void *tmp = nodej->data;
                nodej->data = nodej1->data;
                nodej1->data = tmp;
            }
        }
    }
}
