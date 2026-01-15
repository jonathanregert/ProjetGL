.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 3
istore 1
ldc 7
istore 2
ldc 0
istore 4
ldc 0
istore 6
ldc 2.5
fstore 8
ldc 1
istore 11
ldc 0
istore 12
iload 1
ineg
iload 2
iload 1
ldc 2
iadd
imul
iadd
iload 2
iload 1
ldc 1
iadd
idiv
isub
istore 3
iload 3
i2f
iload 1
iload 2
iadd
i2f
ldc 2.0
fmul
fadd
fstore 9
iload 3
iload 1
iload 2
isub
imul
i2f
iload 1
ldc 1
iadd
i2f
fdiv
fstore 10
ldc "c = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 3
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc "g = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
fload 9
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(F)V
ldc "h = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
fload 10
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(F)V
fload 9
f2i
istore 7
fload 10
ldc 3.7
fadd
f2i
istore 5
ldc "k = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 7
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc "j = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 5
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
iload 1
iload 2
iadd
i2f
ldc 1.5
fmul
f2i
istore 7
ldc "k (nested cast) = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 7
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
fload 9
f2i
fload 10
f2i
if_icmplt L3
goto L0
L3:
fload 8
f2i
ldc 2
if_icmpeq L4
goto L1
L4:
iload 2
ldc 0
if_icmpne L0
goto L1
L0:
ldc 1
goto L2
L1:
ldc 0
L2:
istore 13
ldc "cond = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 13
ifne L5
goto L6
L5:
ldc 1
goto L7
L6:
ldc 0
L7:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
fload 9
f2i
fload 10
f2i
if_icmpgt L8
goto L9
L8:
ldc "g > h"
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
goto L10
L9:
fload 9
f2i
fload 10
f2i
if_icmpeq L11
goto L12
L11:
ldc "g == h"
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
goto L13
L12:
ldc "g < h"
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
L13:
L10:
L14:
fload 8
f2i
ldc 5
if_icmplt L15
goto L16
L15:
iload 6
fload 8
f2i
iadd
istore 6
fload 8
ldc 1.0
fadd
fstore 8
goto L14
L16:
ldc "acc = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 6
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
iload 2
i2f
ldc 1.0
fmul
f2i
dup
istore 1
istore 5
ldc "a = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 1
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc "b = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 2
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc "j = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 5
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc "values: "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
fload 9
f2i
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
ldc ", "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 1
i2f
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(F)V
ldc ", "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 3
i2f
ldc 2.0
fdiv
f2i
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
ldc "acc (hex) = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 6
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc 0
istore 12
iload 12
ifne L20
goto L21
L21:
fload 8
f2i
ldc 5
if_icmpge L17
goto L20
L20:
ldc 0
ifne L17
goto L18
L17:
ldc 1
goto L19
L18:
ldc 0
L19:
istore 11
ldc "ok = "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 11
ifne L22
goto L23
L22:
ldc 1
goto L24
L23:
ldc 0
L24:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
