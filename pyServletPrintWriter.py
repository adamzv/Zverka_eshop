import argparse
import sys
from pathlib import Path
from datetime import datetime


def create_parser():
    parser = argparse.ArgumentParser(description="Spracuje html súbor do podoby využiteľnej v servletoch.\n"
                                                 "napr. out.println(\"<h1>Java PrintWriter</h1>\");")
    parser.add_argument("subor", metavar="subor", nargs=1, help="súbor, ktorého dáta sa majú spracovať")
    const = "pyspw_" + datetime.strftime(datetime.now(), "%Y%m%d%H%M%S") + ".html"
    parser.add_argument("-w", nargs="?", const=const, dest="zapis", required=False, help="voliteľný súbor, do ktorého sa majú zapísať spracované dáta")

    return parser


def spracuj(vstupny_subor, vystupny_subor=None):
    subor_na_citanie = Path(vstupny_subor)

    # ak sa vstupný súbor nenašiel, ukonči spracovanie
    if not subor_na_citanie.is_file():
        print("Súbor na čítanie sa nenašiel")
        return False
    else:
        try:
            vstup = open(vstupny_subor, "r", encoding="utf8")
            vystup = open(vystupny_subor, "a", encoding="utf8")

        # Ak sa pri vstupe nezadá argument -w, tak hodnota atribútu args.zapis bude None
        except TypeError:
            vystup = None
        finally:
            for line in enumerate(vstup):
                line = line[1].rstrip()
                if line == "":
                    line = "out.println();"
                else:
                    line = line.replace("\"", "\\\"")
                    line = f"out.println(\"{line}\");"
                if vystup is not None:
                    vystup.write(line + "\n")
                else:
                    print(line)

            vstup.close()
            if vystup is not None:
                vystup.close()
        return True






def main():
    parser = create_parser()
    args = parser.parse_args()
    print(args)
    spracuj(args.subor[0], args.zapis)


if __name__ == "__main__":
    main()
