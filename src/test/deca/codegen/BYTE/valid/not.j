.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 1
ifne L1
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
ldc 0
ifne L4
goto L3
L3:
ldc 1
goto L5
L4:
ldc 0
L5:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc 1
ldc 2
if_icmplt L7
goto L6
L6:
ldc 1
goto L8
L7:
ldc 0
L8:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
