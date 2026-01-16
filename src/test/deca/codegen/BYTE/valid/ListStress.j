.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 5
.limit locals 10
; Beginning of main (byte)
ldc 1
istore 1
ldc 2
istore 2
ldc 3
istore 3
ldc 4
istore 4
ldc 5
istore 5
ldc "sum="
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 1
iload 2
iadd
iload 3
iadd
iload 4
iadd
iload 5
iadd
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
iload 1
iload 2
iadd
iload 3
iload 4
iadd
imul
iload 5
iload 1
iload 3
iadd
imul
isub
istore 6
iload 1
iload 2
iload 3
iload 4
iload 5
iadd
iadd
iadd
iadd
istore 7
iload 1
iload 2
iload 3
iadd
imul
iload 4
iload 5
isub
isub
iload 1
iload 4
iadd
iload 3
iload 2
isub
imul
iadd
istore 8
ldc "r1="
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 6
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc "r2="
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 7
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc "r3="
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 8
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
iload 6
iload 7
if_icmpgt L4
goto L3
L4:
iload 8
ldc 0
if_icmpne L0
goto L3
L3:
iload 7
ldc 0
if_icmplt L1
goto L0
L0:
ldc 1
goto L2
L1:
ldc 0
L2:
istore 9
ldc "t="
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 9
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
; End of main (byte)
return
.end method
