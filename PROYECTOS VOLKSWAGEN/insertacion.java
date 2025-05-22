PreparedStatement stmt = conn.prepareStatement(
  "INSERT INTO documentos (nombre_archivo, tipo, id_empleado, id_cliente) VALUES (?, ?, ?, ?)");
stmt.setString(1, "reporte_servicio.pdf");
stmt.setString(2, "PDF");
stmt.setInt(3, 2); // ID del empleado
stmt.setInt(4, 5); // ID del cliente
stmt.executeUpdate();
