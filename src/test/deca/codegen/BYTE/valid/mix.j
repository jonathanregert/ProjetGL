.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 6
.limit locals 11
; Beginning of main (byte)
ldc 3
istore 1
ldc 7
istore 2
ldc 2.5
fstore 4
ldc 1
istore 6
ldc 4
istore 8
ldc 0
istore 10
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
fload 4
ldc 2.0
fmul
fadd
fstore 5
ldc "c="
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 3
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc "g="
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
fload 5
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(F)V
iload 3
ldc 0
if_icmpgt L4
goto L3
L4:
iload 1
iload 2
if_icmpgt L3
goto L0
L3:
fload 5
ldc 0.0
fcmpl
ifge L0
goto L1
L0:
ldc 1
goto L2
L1:
ldc 0
L2:
istore 7
iload 7
ifne L5
goto L6
L5:
ldc "branch:T"
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
iload 6
ifne L11
goto L9
L11:
ldc 1
ifne L8
goto L9
L8:
ldc 1
goto L10
L9:
ldc 0
L10:
istore 6
goto L7
L6:
ldc "branch:F"
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
ldc 0
istore 6
L7:
L12:
iload 8
ldc 0
if_icmpgt L15
goto L14
L15:
iload 6
ifne L13
goto L14
L13:
iload 8
istore 9
L16:
iload 9
ldc 0
if_icmpgt L17
goto L18
L17:
iload 10
iload 8
iload 9
iadd
iload 8
ldc 1
isub
imul
iadd
iload 9
ldc 2
imul
isub
istore 10
iload 9
ldc 1
isub
istore 9
goto L16
L18:
iload 8
ldc 1
isub
istore 8
goto L12
L14:
ldc "acc="
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 10
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
