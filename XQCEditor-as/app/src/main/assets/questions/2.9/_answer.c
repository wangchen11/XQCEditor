
#include <stdio.h>
#include <string.h>

char text[100000+1];
int hctoi(char h)
{
  if(h>='A'&&h<='Z')
       return h-'A'+10;
  if(h>='a'&&h<='z')
       return h-'a'+10;
  return h-'0';
}

void HtoO(char *text,int len,int s)
{
  int d=0,i;
  i=0;
  while(text[i]&&i<len)
  {
    d*=16;
    d+=hctoi(text[i]);
    i++;
  }
  if(s)
    printf("%08o",d);
  else
    printf("%o",d);
 
}

int main() {
  int i,j,len,n=0,num,y;
  scanf("%d",&num);
  for(j=0;j<num;j++)
  { 
    scanf("%s",text);
    len=strlen(text);
    n=len/6;
    y=len%6;
    if(y)
      HtoO(text,y,0);
    if(n!=0)
    {
      if(y)
        HtoO (text+y,6,1);
      else
          HtoO (text+y,6,0);
    }
    for(i=1;i<n;i++)
    {
        HtoO (text+i*6+y,6,1);
    }
    printf("\n"); 
  }
  return 0;
}
