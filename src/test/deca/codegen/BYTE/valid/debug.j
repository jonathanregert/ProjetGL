.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 3
.limit locals 3
; Beginning of main (byte)
ldc 3
istore 1
ldc 5
istore 2
iload 1
iload 2
if_icmplt L1
goto L0
L0:
ldc 1
goto L2
L1:
ldc 0
L2:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
