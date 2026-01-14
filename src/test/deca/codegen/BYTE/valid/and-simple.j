.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 1
ifne L3
goto L1
L3:
ldc 1
ifne L0
goto L1
L0:
ldc 1
goto L2
L1:
ldc 0
L2:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
ldc 1
ifne L7
goto L5
L7:
ldc 0
ifne L4
goto L5
L4:
ldc 1
goto L6
L5:
ldc 0
L6:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
ldc 0
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
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
ldc 0
ifne L15
goto L13
L15:
ldc 0
ifne L12
goto L13
L12:
ldc 1
goto L14
L13:
ldc 0
L14:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
; End of main (byte)
return
.end method
