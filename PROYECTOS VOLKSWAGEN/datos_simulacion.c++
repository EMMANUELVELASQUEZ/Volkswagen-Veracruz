#include <iostream>

void convertirDocumento(std::string nombre, std::string tipoDestino) {
    std::cout << "Convirtiendo " << nombre << " a formato " << tipoDestino << "...\n";
    std::cout << "Conversión completada.\n";
}

int main() {
    std::string doc = "documento.docx";
    convertirDocumento(doc, "PDF");
    return 0;
}
