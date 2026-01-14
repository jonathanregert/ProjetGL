.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 3
istore 1
ldc 4
istore 2
iload 1
iload 2
iadd
ldc 2
imul
istore 3
ldc 0
istore 4
iload 3
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
iload 3
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
L0:
iload 4
ldc 5
if_icmplt L1
goto L2
L1:
iload 4
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
iload 4
ldc 1
iadd
istore 4
goto L0
L2:
; End of main (byte)
return
.end method
