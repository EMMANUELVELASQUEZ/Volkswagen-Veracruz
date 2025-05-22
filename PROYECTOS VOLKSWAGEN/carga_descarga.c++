#include <iostream>
#include <filesystem>
namespace fs = std::filesystem;

int main() {
    std::string path = "./documentos/";
    std::cout << "Archivos PDF en la carpeta:\n";

    for (const auto& entry : fs::directory_iterator(path)) {
        if (entry.path().extension() == ".pdf") {
            std::cout << "- " << entry.path().filename() << "\n";
        }
    }

    return 0;
}
