#!/usr/bin/env python3

import re
import sys

BASE = {
    "LOAD": (2, 2), "STORE": (2, 2), "LEA": (0, 0), "PEA": (4, 4),
    "PUSH": (4, 4), "POP": (2, 2),
    "NEW": (16, 16), "DEL": (16, 16),
    "ADD": (2, 2), "SUB": (2, 2), "SHL": (2, 2), "SHR": (2, 2), "OPP": (2, 2),
    "MUL": (20, 20), "DIV": (40, 40), "QUO": (40, 40), "REM": (40, 40),
    "CMP": (2, 2), "INT": (4, 4), "FLOAT": (4, 4),
    "BRA": (5, 5), "BSR": (9, 9), "RTS": (8, 8),
    "RINT": (16, 16), "RFLOAT": (16, 16),
    "WINT": (16, 16), "WFLOAT": (16, 16), "WFLOATX": (16, 16),
    "RUTF8": (16, 16), "WUTF8": (16, 16),
    "WSTR": (16, 16), "WNL": (14, 14),
    "ADDSP": (4, 4), "SUBSP": (4, 4), "TSTO": (4, 4),
    "HALT": (1, 1), "ERROR": (1, 1),
    "SCLK": (2, 2), "CLK": (16, 16),
    "FMA": (21, 21),
}

_re_label = re.compile(r"^([A-Za-z][A-Za-z0-9_\.]*)\s*:\s*(.*)$")
_re_reg = re.compile(r"^(R\d+|SP|LB|GB)$", re.IGNORECASE)
_re_dxx_rm = re.compile(r"^[+-]?\d+\(([^,\)]+),\s*(R\d+)\)$", re.IGNORECASE)
_re_dxx = re.compile(r"^[+-]?\d+\(([^,\)]+)\)$")


def base_cost(op):
    op = op.upper()

    # Bcc: BEQ, BNE, BOV, ... (exclude BRA/BSR)
    if op.startswith("B") and len(op) == 3 and op not in ("BRA", "BSR"):
        return (4, 5)

    # Scc: SEQ, SNE, ...
    if op.startswith("S") and len(op) == 3:
        return (2, 3)

    # SETROUND
    if op.startswith("SETROUND"):
        return (20, 20)

    return BASE.get(op)


def split_operands(s):
    out = []
    buf = []
    in_str = False
    i = 0
    while i < len(s):
        c = s[i]
        if c == '"':
            # doubled quotes "" dans string
            if in_str and i + 1 < len(s) and s[i + 1] == '"':
                buf.append('"')
                i += 2
                continue
            in_str = not in_str
            buf.append('"')
            i += 1
            continue
        if c == ',' and not in_str:
            part = "".join(buf).strip()
            if part:
                out.append(part)
            buf = []
            i += 1
            continue
        buf.append(c)
        i += 1
    part = "".join(buf).strip()
    if part:
        out.append(part)
    return out


def access_cost(op):
    op = op.strip()

    # Rm
    if _re_reg.match(op):
        return (0, 0)

    # d(XX,Rm)
    if _re_dxx_rm.match(op):
        return (5, 5)

    # d(XX)
    if _re_dxx.match(op):
        return (4, 4)

    # #d
    if op.startswith("#"):
        return (2, 2)

    # "..."
    if len(op) >= 2 and op[0] == '"' and op[-1] == '"':
        s = op[1:-1].replace('""', '"')
        return (2 * len(s), 2 * len(s))

    # etiq
    return (2, 2)


def estimate(lines):
    mn = 0
    mx = 0
    n = 0

    for raw in lines:
        line = raw.split(";", 1)[0].strip()
        if not line:
            continue

        while True:
            m = _re_label.match(line)
            if not m:
                break
            line = m.group(2).strip()
            if not line:
                break
        if not line:
            continue

        parts = line.split(None, 1)
        opcode = parts[0].upper()
        operands = split_operands(parts[1]) if len(parts) > 1 else []

        bc = base_cost(opcode)
        if bc is None:
            raise SystemExit(f"Unknown opcode: {opcode}")

        bmn, bmx = bc
        amn = 0
        amx = 0
        for o in operands:
            cmn, cmx = access_cost(o)
            amn += cmn
            amx += cmx

        mn += bmn + amn
        mx += bmx + amx
        n += 1

    return n, mn, mx


def main():
    if len(sys.argv) != 2:
        print("usage: ima_estimate_cycles.py file.ass", file=sys.stderr)
        return 2

    path = sys.argv[1]
    with open(path, "r", encoding="utf-8") as f:
        instr, mn, mx = estimate(f.readlines())

    print(f"{instr}\t{mn}\t{mx}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
