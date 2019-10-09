#define BOOL  int
#define FALSE 0
#define TRUE  1

//声明链表节点类型
typedef struct LinkedListNode_St LinkedListNode; 

//声明链表类型
typedef struct LinkedList_St LinkedList;

//定义比较函数类型，比较函数用于查找和排序
typedef int(*compare_fp)(const void *obj1,const void *obj2);

//创建一个链表
LinkedList* newLinkedList();

//将链表克隆（不会克隆里面的数据）
LinkedList* LinkedList_clone(LinkedList* thiz);


//删除一个链表（不会free里面的数据）
void deleteLinkedList(LinkedList* thiz);

//判断链表是否为空
BOOL LinkedList_isEmpty(LinkedList* thiz);

//得到链表长度
int  LinkedList_size(LinkedList* thiz);

//清空链表（不会free里面的数据）
void LinkedList_clean(LinkedList* thiz);

//将另一个链表全部添加进来
BOOL LinkedList_addAll(LinkedList* thiz,LinkedList* other);

//在链表的指定位置插入数据
BOOL LinkedList_addAt(LinkedList* thiz,const void *data,int pos);

//在链表的末尾插入数据
BOOL LinkedList_addLast (LinkedList* thiz,const void *data);

//在链表的开头插入数据
BOOL LinkedList_addFirst(LinkedList* thiz,const void *data);

//删除数据，通过cmp函数来比较如果cmp函数返回0则移除它。如果cmp为NULL则直接比较指针
BOOL LinkedList_remove(LinkedList* thiz,const void *data,compare_fp cmp);

//从链表删除指定位置的数据并返回它
void *LinkedList_removeAt(LinkedList* thiz,int pos);

//获取链表指定位置的数据
void *LinkedList_get(LinkedList* thiz,int pos);

//push
BOOL LinkedList_push(LinkedList* thiz,const void *data);

//pop
void *LinkedList_pop(LinkedList* thiz);

//peek
void *LinkedList_peek(LinkedList* thiz);

//enqueue
BOOL LinkedList_enqueue(LinkedList* thiz,const void *data);

//dequeue
void *LinkedList_dequeue(LinkedList* thiz);

//将链表转换为数据，返回值需要free
void **LinkedList_toArray(LinkedList* thiz);

//查找数据，通过cmp函数来比较如果cmp函数返回0则找到了。如果cmp为NULL则直接比较指针
int  LinkedList_indexOf(LinkedList* thiz,const void *data,compare_fp cmp);

//排序，通过cmp函数来比较,通过cmp函数的返回值判断是否交换数据，cmp不能为NULL
void LinkedList_sort(LinkedList* thiz,compare_fp cmp);
void testLinkedList();
