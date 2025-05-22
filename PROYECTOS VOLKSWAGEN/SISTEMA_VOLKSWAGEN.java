/*
 * Paquete: com.volkswagen.enterprise.model
 * Contiene clases de entidades y DTOs
 */
package com.volkswagen.enterprise.model;

/**
 * Entidad Producto
 */
public class Producto {
    private final long id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;

    public Producto(long id, String nombre, String descripcion, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters y setters
    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return String.format("Producto{id=%d, nombre='%s', descripcion='%s', precio=%.2f, stock=%d}",
                id, nombre, descripcion, precio, stock);
    }
}

/**
 * DTO para creación o actualización de Producto
 */
public class ProductoDTO {
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;

    public ProductoDTO(String nombre, String descripcion, double precio, int stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
}

/*
 * Paquete: com.volkswagen.enterprise.exception
 * Clases de excepción personalizada
 */
package com.volkswagen.enterprise.exception;

/**
 * Excepción general para errores del sistema Volkswagen
 */
public class SistemaException extends Exception {
    public SistemaException(String message) {
        super(message);
    }
}

/*
 * Paquete: com.volkswagen.enterprise.dao
 * Interfaces y clases DAO
 */
package com.volkswagen.enterprise.dao;

import com.volkswagen.enterprise.model.Producto;
import java.util.List;
import java.util.Optional;

/**
 * DAO genérico para Productos
 */
public interface ProductoDAO {
    Producto guardar(Producto producto);
    Optional<Producto> buscarPorId(long id);
    Producto actualizar(Producto producto);
    void eliminar(long id);
    List<Producto> listarTodos();
}

package com.volkswagen.enterprise.dao.impl;

import com.volkswagen.enterprise.dao.ProductoDAO;
import com.volkswagen.enterprise.model.Producto;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación en memoria simulando DB
 */
public class ProductoDAOMemoria implements ProductoDAO {
    private final Map<Long, Producto> productos = new HashMap<>();
    private final AtomicLong idGenerador = new AtomicLong(1000);

    @Override
    public Producto guardar(Producto producto) {
        long id = idGenerador.incrementAndGet();
        Producto p = new Producto(id, producto.getNombre(), producto.getDescripcion(), producto.getPrecio(), producto.getStock());
        productos.put(id, p);
        return p;
    }

    @Override
    public Optional<Producto> buscarPorId(long id) {
        return Optional.ofNullable(productos.get(id));
    }

    @Override
    public Producto actualizar(Producto producto) {
        productos.put(producto.getId(), producto);
        return producto;
    }

    @Override
    public void eliminar(long id) {
        productos.remove(id);
    }

    @Override
    public List<Producto> listarTodos() {
        return new ArrayList<>(productos.values());
    }
}

/*
 * Paquete: com.volkswagen.enterprise.service
 * Lógica de negocio y validaciones
 */
package com.volkswagen.enterprise.service;

import com.volkswagen.enterprise.dao.ProductoDAO;
import com.volkswagen.enterprise.exception.SistemaException;
import com.volkswagen.enterprise.model.Producto;
import com.volkswagen.enterprise.model.ProductoDTO;

import java.util.List;

/**
 * Servicio profesional para gestionar Productos
 */
public class ProductoService {
    private final ProductoDAO productoDAO;

    public ProductoService(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public Producto crearProducto(ProductoDTO dto) throws SistemaException {
        validarProductoDTO(dto);
        Producto producto = new Producto(0, dto.getNombre(), dto.getDescripcion(), dto.getPrecio(), dto.getStock());
        return productoDAO.guardar(producto);
    }

    public Producto actualizarProducto(long id, ProductoDTO dto) throws SistemaException {
        validarProductoDTO(dto);
        Producto existente = productoDAO.buscarPorId(id)
                .orElseThrow(() -> new SistemaException("Producto no encontrado: ID=" + id));

        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setPrecio(dto.getPrecio());
        existente.setStock(dto.getStock());

        return productoDAO.actualizar(existente);
    }

    public void eliminarProducto(long id) throws SistemaException {
        if (!productoDAO.buscarPorId(id).isPresent()) {
            throw new SistemaException("Producto no existe: ID=" + id);
        }
        productoDAO.eliminar(id);
    }

    public List<Producto> listarProductos() {
        return productoDAO.listarTodos();
    }

    private void validarProductoDTO(ProductoDTO dto) throws SistemaException {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new SistemaException("El nombre del producto es obligatorio");
        }
        if (dto.getPrecio() < 0) {
            throw new SistemaException("El precio no puede ser negativo");
        }
        if (dto.getStock() < 0) {
            throw new SistemaException("El stock no puede ser negativo");
        }
    }
}

/*
 * Paquete: com.volkswagen.enterprise.controller
 * Controladores para orquestar servicios
 */
package com.volkswagen.enterprise.controller;

import com.volkswagen.enterprise.exception.SistemaException;
import com.volkswagen.enterprise.model.Producto;
import com.volkswagen.enterprise.model.ProductoDTO;
import com.volkswagen.enterprise.service.ProductoService;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador principal del sistema Volkswagen
 */
public class VolkswagenController {
    private static final Logger logger = Logger.getLogger(VolkswagenController.class.getName());

    private final ProductoService productoService;

    public VolkswagenController(ProductoService productoService) {
        this.productoService = productoService;
    }

    public void ejecutarDemo() {
        try {
            logger.info("=== Inicio demo sistema Volkswagen ===");

            ProductoDTO nuevoProducto = new ProductoDTO("Motor Turbo TSI", "Motor de alta eficiencia VW", 2500.0, 200);
            Producto creado = productoService.crearProducto(nuevoProducto);
            logger.info("Producto creado: " + creado);

            ProductoDTO actualizarDTO = new ProductoDTO("Motor Turbo TSI - Edición Limitada", "Motor con mejoras de rendimiento", 2700.0, 180);
            Producto actualizado = productoService.actualizarProducto(creado.getId(), actualizarDTO);
            logger.info("Producto actualizado: " + actualizado);

            List<Producto> productos = productoService.listarProductos();
            logger.info("Lista de productos actuales:");
            productos.forEach(p -> logger.info(p.toString()));

            productoService.eliminarProducto(actualizado.getId());
            logger.info("Producto eliminado con ID: " + actualizado.getId());

            logger.info("=== Demo finalizado con éxito ===");
        } catch (SistemaException ex) {
            logger.log(Level.SEVERE, "Error en sistema: " + ex.getMessage(), ex);
        }
    }
}

/*
 * Paquete raíz
 */
package com.volkswagen.enterprise;

import com.volkswagen.enterprise.controller.VolkswagenController;
import com.volkswagen.enterprise.dao.ProductoDAO;
import com.volkswagen.enterprise.dao.impl.ProductoDAOMemoria;
import com.volkswagen.enterprise.service.ProductoService;

/**
 * Clase principal para ejecutar demo sistema Volkswagen
 */
public class Main {
    public static void main(String[] args) {
        ProductoDAO productoDAO = new ProductoDAOMemoria();
        ProductoService productoService = new ProductoService(productoDAO);
        VolkswagenController controller = new VolkswagenController(productoService);

        controller.ejecutarDemo();
    }
}

