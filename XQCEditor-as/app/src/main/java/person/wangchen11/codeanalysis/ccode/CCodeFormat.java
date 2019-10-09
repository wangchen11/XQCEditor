package person.wangchen11.codeanalysis.ccode;

import java.util.ArrayList;

public class CCodeFormat {
	private CCodeParser mCCodeParser = null;
	public CCodeFormat(CCodeParser codeParser) {
		mCCodeParser = codeParser;
	}
	
	
	public String getFormatedCode()
	{
		try {
			return getFormatedCodeEx();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getFormatedCodeEx()
	{
		ArrayList<CCodeSpan> codeSpans = mCCodeParser.getCodeSpans();
		StringBuilder stringBuilder = new StringBuilder();
		int level = 0;
		boolean isIfWhileORFor = false;
		int inNumber = 0;
		boolean thisLineHasExtern = false;;
		
		for(int i=0;i<codeSpans.size();i++)
		{
			CCodeSpan codeSpan = codeSpans.get(i);
			CCodeSpan nextSpan = null;
			if(i+1<codeSpans.size())
				nextSpan = codeSpans.get(i+1);
			
			if(codeSpan.mContent.equals("case")||codeSpan.mContent.equals("default"))
			{
				if(stringBuilder.length()>0)
				{
					if(stringBuilder.charAt(stringBuilder.length()-1) == '\t')
					{
						stringBuilder.deleteCharAt(stringBuilder.length()-1);
					}
				}
			}
			
			if(codeSpan.mContent.equals("="))
			{
				stringBuilder.append(" ");
			}
			
			stringBuilder.append(codeSpan.mContent);

			if(codeSpan.mContent.equals("="))
			{
				stringBuilder.append(" ");
			}
			
			if(nextSpan!=null)
			{
				if( (
						codeSpan.mType == CCodeSpan.TYPE_KEY_WORDS 
						||codeSpan.mType == CCodeSpan.TYPE_WORDS
						||codeSpan.mType == CCodeSpan.TYPE_PRO_KEY_WORDS
						||codeSpan.mType == CCodeSpan.TYPE_CONSTANT
						||codeSpan.mType == CCodeSpan.TYPE_COMMENTS
						/*
						||(codeSpan.mType == CCodeSpan.TYPE_NONE&&
							(
								codeSpan.mContent.equals("&&")
								||codeSpan.mContent.equals("||")
								||codeSpan.mContent.equals("<<")
								||codeSpan.mContent.equals(">>")
								||codeSpan.mContent.equals("==")
								||codeSpan.mContent.equals(">=")
								||codeSpan.mContent.equals("<=")
								||codeSpan.mContent.equals("!=")
								||codeSpan.mContent.equals("+=")
								||codeSpan.mContent.equals("-=")
								||codeSpan.mContent.equals("*=")
								||codeSpan.mContent.equals("/=")
								||codeSpan.mContent.equals("&=")
								||codeSpan.mContent.equals("|=")
								||codeSpan.mContent.equals("<<=")
								||codeSpan.mContent.equals(">>=")
								||codeSpan.mContent.equals("^=")
							) )*/
					) && 
					(
						nextSpan.mType == CCodeSpan.TYPE_KEY_WORDS 
						||nextSpan.mType == CCodeSpan.TYPE_WORDS
						||nextSpan.mType == CCodeSpan.TYPE_PRO_KEY_WORDS
						||nextSpan.mType == CCodeSpan.TYPE_CONSTANT
						||nextSpan.mType == CCodeSpan.TYPE_COMMENTS
						/*
						||(nextSpan.mType == CCodeSpan.TYPE_NONE&&
							(
								nextSpan.mContent.equals("&&")
								||nextSpan.mContent.equals("||")
								||nextSpan.mContent.equals("<<")
								||nextSpan.mContent.equals(">>")
								||nextSpan.mContent.equals("==")
								||nextSpan.mContent.equals(">=")
								||nextSpan.mContent.equals("<=")
								||nextSpan.mContent.equals("!=")
								||nextSpan.mContent.equals("+=")
								||nextSpan.mContent.equals("-=")
								||nextSpan.mContent.equals("*=")
								||nextSpan.mContent.equals("/=")
								||nextSpan.mContent.equals("&=")
								||nextSpan.mContent.equals("|=")
								||nextSpan.mContent.equals("<<=")
								||nextSpan.mContent.equals(">>=")
								||nextSpan.mContent.equals("^=")
							) )*/
					) )
				{
					stringBuilder.append(" ");
				}
			}
			
			if(codeSpan.mContent.equals("{"))
			{
				if(thisLineHasExtern)
				{
					
				}
				else
				{
					level++;
					if( nextSpan!=null && (!nextSpan.mContent.equals("\n")) )
					{
						stringBuilder.append("\n");
						stringBuilder.append(makeSameChars(level,'\t'));
					}
				}
			}
			else
			if(codeSpan.mContent.equals("}"))
			{
				level--;
				if(level<0)
					level = 0;
			}
			else
			if(codeSpan.mContent.equals("if")||codeSpan.mContent.equals("while")||codeSpan.mContent.equals("for"))
			{
				isIfWhileORFor = true;
				inNumber = 0;
				level++;
			}
			else
			if(codeSpan.mContent.equals("else"))
			{
				if((nextSpan.mContent.equals("if")))
				{
					
				}
				else
				if((!nextSpan.mContent.equals("\n")))
				{
					if(!nextSpan.mContent.equals("{"))
					{
						stringBuilder.append("\n");
						stringBuilder.append(makeSameChars(level+1,'\t'));
					}
				}
				else
				{
					stringBuilder.append("\n");
					stringBuilder.append(makeSameChars(level,'\t'));
					i++;
					if( (i+1<codeSpans.size())&&(!codeSpans.get(i+1).mContent.equals("{")) )
					{
						stringBuilder.append("\t");
					}
				}
			}
			else
			if(codeSpan.mContent.equals("extern"))
			{
				thisLineHasExtern = true;
			}
			else
			if(codeSpan.mContent.equals("("))
			{
				if(isIfWhileORFor)
				{
					inNumber++;
				}
				level++;
			}
			else
			if(codeSpan.mContent.equals(")"))
			{
				level--;
				if(level<0)
					level = 0;
				
				if(isIfWhileORFor)
				{
					inNumber--;
					if(inNumber<=0)
					{
						isIfWhileORFor = false;
						inNumber = 0;
						level--;
						
						CCodeSpan tempSpan = null;
						for(;i+1<codeSpans.size();i++)
						{
							tempSpan = codeSpans.get(i+1);
							if(!(tempSpan.mType == CCodeSpan.TYPE_COMMENTS))
								break;
							else
							{
								stringBuilder.append(tempSpan.mContent);
							}
						}
						
						if(tempSpan!=null )
						{
							if((!tempSpan.mContent.equals("\n")))
							{
								if(!tempSpan.mContent.equals("{"))
								{
									stringBuilder.append("\n");
									stringBuilder.append(makeSameChars(level+1,'\t'));
								}
							}
							else
							{
								stringBuilder.append("\n");
								stringBuilder.append(makeSameChars(level,'\t'));
								i++;
								if( (i+1<codeSpans.size())&&(!codeSpans.get(i+1).mContent.equals("{")) )
								{
									stringBuilder.append("\t");
								}
							}
						}
					}
				}
				
			}else
			if(codeSpan.mContent.equals(";"))
			{
				if(isIfWhileORFor)
					;
				else
				{
					CCodeSpan tempSpan = null;
					for(;i+1<codeSpans.size();i++)
					{
						tempSpan = codeSpans.get(i+1);
						if(!(tempSpan.mType == CCodeSpan.TYPE_COMMENTS))
							break;
						else
						{
							stringBuilder.append(tempSpan.mContent);
						}
					}
					if( tempSpan!=null && (!tempSpan.mContent.equals("\n")) )
					{
						stringBuilder.append("\n");
						stringBuilder.append(makeSameChars(level,'\t'));
					}
				}
			}
			
			if(codeSpan.mContent.equals("\n"))
			{
				thisLineHasExtern = false;
				if( nextSpan!=null && (nextSpan.mContent.equals(")")) )
				{
					stringBuilder.append(makeSameChars(level,'\t'));
				}
				else
				if( nextSpan!=null && (nextSpan.mContent.equals("}")) )
				{
					stringBuilder.append(makeSameChars(level-1,'\t'));
				}
				else
					stringBuilder.append(makeSameChars(level,'\t'));
			}
			else
			{
				//stringBuilder.append(" ");
				if( nextSpan!=null && (nextSpan.mContent.equals("}")) )
				{
					stringBuilder.append("\n");
					stringBuilder.append(makeSameChars(level-1,'\t'));
				}
			}
		}
		return stringBuilder.toString();
	}
	
	private static String makeSameChars(int n,char ch)
	{
		String str = "";
		for(;n>0;n--)
		{
			str+=ch;
		}
		return str;
	}
}
