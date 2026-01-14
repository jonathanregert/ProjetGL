.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 1
ldc 2
ldc 3
imul
iadd
ldc 5
if_icmpgt L3
goto L1
L3:
ldc 4
ldc 2
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
ldc 1
ifne L4
goto L7
L7:
ldc 0
ifne L4
goto L5
L4:
ldc 42
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
goto L6
L5:
L6:
L8:
ldc 0
ifne L9
goto L11
L11:
ldc 0
ifne L9
goto L10
L9:
ldc 0
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
goto L8
L10:
ldc 1
ineg
ineg
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
