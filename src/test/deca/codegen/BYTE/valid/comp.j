.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 1
ldc 1
if_icmpeq L0
goto L1
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
ldc 2
if_icmpne L3
goto L4
L3:
ldc 1
goto L5
L4:
ldc 0
L5:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc 3
ldc 2
if_icmpgt L6
goto L7
L6:
ldc 1
goto L8
L7:
ldc 0
L8:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc 3
ldc 3
if_icmpge L9
goto L10
L9:
ldc 1
goto L11
L10:
ldc 0
L11:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc 1
ldc 2
if_icmplt L12
goto L13
L12:
ldc 1
goto L14
L13:
ldc 0
L14:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc 2
ldc 2
if_icmple L15
goto L16
L15:
ldc 1
goto L17
L16:
ldc 0
L17:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
