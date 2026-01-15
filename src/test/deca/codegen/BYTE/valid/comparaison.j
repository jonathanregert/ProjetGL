.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 0
istore 1
L0:
iload 1
ldc 5
if_icmplt L1
goto L2
L1:
iload 1
ldc 2
if_icmpeq L3
goto L4
L3:
ldc "C'est deux"
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
goto L5
L4:
iload 1
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
L5:
iload 1
ldc 1
iadd
istore 1
goto L0
L2:
; End of main (byte)
return
.end method
