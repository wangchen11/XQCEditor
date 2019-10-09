#include <stdio.h>
#include <stdlib.h> 

void sort(int *ary,int len)
{
  int temp,i,j;

  for(i=0;i<len-1;i++)
  {
    for(j=0;j<len-i;j++)
    {
      if(ary[j]>ary[j+1])
      {
        temp=ary[j+1];
        ary[j+1]=ary[j];
        ary[j]=temp;
      }
    }
  }
}

int main() {
  int n,*ary,i,data;
  scanf("%d",&n);
  ary=(int *)malloc(sizeof(int)*n);
  for(i=0;i<n;i++)
  {
    scanf("%d",&data);
    ary[i]=data;
  }
  sort(ary,n);
  for(i=0;i<n;i++)
  {
    printf("%d ",ary[i]);
  }
  free(ary);
  return 0;

}
