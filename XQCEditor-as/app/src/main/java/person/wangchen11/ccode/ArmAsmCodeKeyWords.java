package person.wangchen11.ccode;

public class ArmAsmCodeKeyWords {
	public static String []mKeyWord={
		"b",
		"bl",
		"blx",
		"bx",
		"mov",
		"mvn",
		"cmp",
		"cmn",
		"tst",
		"teq",
		"add",
		"adc",
		"sub",
		"c",
		"r0",
		"r1",
		"r2",
		"r3",
		"r4",
		"r5",
		"r6",
		"r7",
		"r8",
		"r9",
		"r10",
		"r11",
		"r12",
		"r12",
		"r14",
		"r15",
		"fp",
		"sp",
		"pc",
		"rsc",
		"and",
		"orr",
		"eor",
		"bic",
		"mul",
		"mla",
		"smull",
		"smlal",
		"umull",
		"umlal",
		"mrs",
		"msr",
		"ldr",
		"ldrb",
		"ldrh",
		"str",
		"strb",
		"strh",
		"ldm",
		"stmfd",
		"ldmfd",
		"stmed",
		"ldmed",
		"stmfa",
		"ldmfa",
		"stmea",
		"ldmea",
		"swp",
		"swpb",
		"lsl",
		"lsr",
		"asr",
		"ror",
		"rrx",
		"cdp",
		"ldc",
		"stc",
		"mcr",
		"mrc",
		"swi",
		"bkpt",
		"gbla",
		"gbll",
		"gbls",
		"lcla",
		"lcll",
		"lcls",
		"seta",
		"setl",
		"sets",
		"rlist",
		"dcb",
		"dcw",
		"dcwu",
		"dcd",
		"dcdu",
		"dcfd",
		"dcfdu",
		"dcfs",
		"dcfsu",
		"dcq",
		"dcqu",
		"space",
		"map",
		"filed",
		"while",
		"wend",
		"mend",
		"mexit",
		"area",
		"readonly",
		"common",
		"code16",
		"code32",
		"entry",
		"export",
		"import",
		"extern",
		"get",
		"incbin",
		"rn",
		"rout",
		"abort",
		"align",
		"absexpr1",
		"absexpr2",
		"if",
		"else",
		"endif",
		"include",
		"comm",
		"data",
		"equ",
		"global",
		"ascii",
		"byte",
		"short",
		"int",
		"long",
		"word",
		"macro",
		"endm",
		"req",
		"code",
		"ltorg",
		"nop",
		"ldp",
		"adr",
		"adrl",
		
		"arch",
		"fpu",
		"eabi_attribute",
		"arm",
		"syntax",
		"file",
		"section",
		"text",
		"type",
		"function",
		"size",
		"ident",
		"section"
	};

	public static char [][]mKeyWord_Char=null;
	static {
		mKeyWord_Char=new char[mKeyWord.length][];
		for(int i=0;i<mKeyWord.length;i++)
		{
			mKeyWord_Char[i] = mKeyWord[i].toCharArray();
		}
	};

}
/*
b
bl
blx
bx
mov
mvn
cmp
cmn
tst
teq
add
adc
sub
c
r0
r1
r2
r3
r4
r5
r6
r7
r8
r9
r10
r11
r12
r12
r14
r15
pc
rsc
and
orr
eor
bic
mul
mla
smull
smlal
umull
umlal
mrs
msr
ldr
ldrb
ldrh
str
strb
strh
ldm
stmfd
ldmfd
stmed
ldmed
stmfa
ldmfa
stmea
ldmea
swp
swpb
lsl
lsr
asr
ror
rrx
cdp
ldc
stc
mcr
mrc
swi
bkpt
gbla
gbll
gbls
lcla
lcll
lcls
seta
setl
sets
rlist
dcb
dcw
dcwu
dcd
dcdu
dcfd
dcfdu
dcfs
dcfsu
dcq
dcqu
space
map
filed
if
else
endif
while
wend
macro
mend
mexit
area
data
readonly
align
common
code16
code32
entry
equ
export
global
import
extern
get
include
incbin
rn
rout
abort
align
absexpr1
absexpr2
if
else
endif
include
comm
data
equ
global
ascii
byte
short
int
long
word
macro
endm
req
code
ltorg
nop
ldp
adr
adrl
 */

/*
B
BL
BLX
BX
MOV
MVN
CMP
CMN
TST
TEQ
ADD
ADC
C
R0
R1
R2
R3
R4
R5
R6
R7
R8
R9
R10
R11
R12
R12
R14
R15
PC
RSC
AND
ORR
EOR
BIC
MUL
MLA
SMULL
SMLAL
UMULL
UMLAL
MRS
MSR
LDR
LDRB
LDRH
STR
STRB
STRH
LDM
STMFD
LDMFD
STMED
LDMED
STMFA
LDMFA
STMEA
LDMEA
SWP
SWPB
LSL
LSR
ASR
ROR
RRX
CDP
LDC
STC
MCR
MRC
SWI
BKPT
GBLA
GBLL
GBLS
LCLA
LCLL
LCLS
SETA
SETL
SETS
RLIST
DCB
DCW
DCWU
DCD
DCDU
DCFD
DCFDU
DCFS
DCFSU
DCQ
DCQU
SPACE
MAP
FILED
IF
ELSE
ENDIF
WHILE
WEND
MACRO
MEND
MEXIT
AREA
DATA
READONLY
ALIGN
COMMON
CODE16
CODE32
ENTRY
EQU
EXPORT
GLOBAL
IMPORT
EXTERN
GET
INCLUDE
INCBIN
RN
ROUT
abort
align
absexpr1
absexpr2
if
else
endif
include
comm
data
equ
global
ascii
byte
short
int
long
word
macro
endm
req
code
ltorg
NOP
LDP
ADR
ADRL

 */






