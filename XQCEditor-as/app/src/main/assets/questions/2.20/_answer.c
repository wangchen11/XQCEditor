#include <stdio.h>
int issu(int n)
{
  int k, upperBound=n/2;
    if(n==2)
        return 1;
    if(n%2==0)
        return 0;
  for(k=3; k<=upperBound; k+=2)
  {
  upperBound=n/k;
  if(n%k==0)
  return 0;
  }
  return 1;
}
int next(int now)
{
    while(1)
    {
        now++;
        if(issu(now))
            return now;
    }
    return now;
}
int main()
{
   int now=1,i,n=0,sum;
   scanf("%d",&n);
   sum=1;
   for(i=0;i<n;i++)
   {
       now=next(now);
       sum*=now;
       sum%=50000;
   }
   printf("%d",sum);
   return 0;
}