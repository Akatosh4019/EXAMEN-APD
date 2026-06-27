from math import atan2, cos, sin, pi
from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.pagesizes import A4, landscape
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.platypus import Paragraph
from reportlab.pdfgen import canvas


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "docs" / "DIAGRAMAS_C4.pdf"

PAGE_W, PAGE_H = landscape(A4)

BLUE = colors.HexColor("#2563eb")
GREEN = colors.HexColor("#059669")
PURPLE = colors.HexColor("#7c3aed")
ORANGE = colors.HexColor("#ea580c")
GRAY = colors.HexColor("#4b5563")
LIGHT_BLUE = colors.HexColor("#e8f1ff")
LIGHT_GREEN = colors.HexColor("#ecfdf5")
LIGHT_PURPLE = colors.HexColor("#f5f3ff")
LIGHT_ORANGE = colors.HexColor("#fff7ed")
LIGHT_GRAY = colors.HexColor("#f3f4f6")
DARK = colors.HexColor("#111827")

styles = getSampleStyleSheet()
BOX_STYLE = ParagraphStyle(
    "Box",
    parent=styles["Normal"],
    fontName="Helvetica",
    fontSize=8.5,
    leading=10.5,
    alignment=1,
    textColor=DARK,
)
SMALL_STYLE = ParagraphStyle(
    "Small",
    parent=styles["Normal"],
    fontName="Helvetica",
    fontSize=7.5,
    leading=9,
    alignment=1,
    textColor=DARK,
)


def draw_header(c, title, subtitle, page):
    c.setFillColor(DARK)
    c.setFont("Helvetica-Bold", 20)
    c.drawString(1.4 * cm, PAGE_H - 1.25 * cm, title)
    c.setFont("Helvetica", 9)
    c.setFillColor(GRAY)
    c.drawString(1.4 * cm, PAGE_H - 1.75 * cm, subtitle)
    c.setStrokeColor(colors.HexColor("#d1d5db"))
    c.line(1.4 * cm, PAGE_H - 2.05 * cm, PAGE_W - 1.4 * cm, PAGE_H - 2.05 * cm)
    c.setFont("Helvetica", 8)
    c.setFillColor(GRAY)
    c.drawRightString(PAGE_W - 1.4 * cm, 0.9 * cm, f"Pagina {page}")


def paragraph(c, text, x, y, w, h, style=BOX_STYLE):
    p = Paragraph(text, style)
    p.wrapOn(c, w, h)
    p.drawOn(c, x, y + (h - p.height) / 2)


def box(c, x, y, w, h, title, body="", fill=LIGHT_GREEN, stroke=GREEN):
    c.setFillColor(fill)
    c.setStrokeColor(stroke)
    c.setLineWidth(1.5)
    c.roundRect(x, y, w, h, 8, stroke=1, fill=1)
    text = f"<b>{title}</b>"
    if body:
        text += f"<br/>{body}"
    paragraph(c, text, x + 0.15 * cm, y + 0.12 * cm, w - 0.3 * cm, h - 0.24 * cm)


def arrow(c, x1, y1, x2, y2, label=None, color=GRAY):
    c.setStrokeColor(color)
    c.setFillColor(color)
    c.setLineWidth(1.2)
    c.line(x1, y1, x2, y2)
    angle = atan2(y2 - y1, x2 - x1)
    size = 6
    p1 = (x2 - size * cos(angle - pi / 6), y2 - size * sin(angle - pi / 6))
    p2 = (x2 - size * cos(angle + pi / 6), y2 - size * sin(angle + pi / 6))
    c.line(x2, y2, p1[0], p1[1])
    c.line(x2, y2, p2[0], p2[1])
    if label:
        c.setFillColor(DARK)
        c.setFont("Helvetica", 7)
        c.drawCentredString((x1 + x2) / 2, (y1 + y2) / 2 + 7, label)


def cover(c):
    c.setFillColor(colors.HexColor("#0f172a"))
    c.rect(0, 0, PAGE_W, PAGE_H, fill=1, stroke=0)
    c.setFillColor(colors.white)
    c.setFont("Helvetica-Bold", 30)
    c.drawString(2.0 * cm, PAGE_H - 4.0 * cm, "Diagramas C4")
    c.setFont("Helvetica-Bold", 18)
    c.drawString(2.0 * cm, PAGE_H - 5.2 * cm, "Proyecto de Microservicios con Saga")
    c.setFont("Helvetica", 11)
    c.drawString(2.0 * cm, PAGE_H - 6.2 * cm, "Quarkus | API Gateway | Consul | Docker | Bases de datos por microservicio")

    box(c, 2.0 * cm, 3.1 * cm, 7.0 * cm, 2.5 * cm, "Patron aplicado", "Saga Orchestration", LIGHT_PURPLE, PURPLE)
    box(c, 10.2 * cm, 3.1 * cm, 7.0 * cm, 2.5 * cm, "Orquestador", "ms.ventas", LIGHT_GREEN, GREEN)
    box(c, 18.4 * cm, 3.1 * cm, 7.0 * cm, 2.5 * cm, "Compensacion", "Restaurar stock si falla la venta", LIGHT_ORANGE, ORANGE)
    c.showPage()


def page_c1(c):
    draw_header(c, "C1 - Diagrama de Contexto", "Vista general del sistema y sus usuarios.", 2)
    user = (2.0 * cm, 8.5 * cm, 5.5 * cm, 2.4 * cm)
    system = (11.0 * cm, 7.8 * cm, 7.2 * cm, 3.3 * cm)
    auth = (21.2 * cm, 8.5 * cm, 5.5 * cm, 2.4 * cm)
    box(c, *user, "Usuario / Administrador", "Consume el sistema desde Postman o frontend", LIGHT_BLUE, BLUE)
    box(c, *system, "Sistema de Gestion de Ventas", "Microservicios con Quarkus y consistencia mediante Saga", LIGHT_GREEN, GREEN)
    box(c, *auth, "Autenticacion", "Login y token para rutas protegidas", LIGHT_ORANGE, ORANGE)
    arrow(c, user[0] + user[2], user[1] + user[3] / 2, system[0], system[1] + system[3] / 2, "HTTP")
    arrow(c, system[0] + system[2], system[1] + system[3] / 2, auth[0], auth[1] + auth[3] / 2, "Token")
    c.showPage()


def page_c2(c):
    draw_header(c, "C2 - Diagrama de Contenedores", "Microservicios, gateway, infraestructura y bases de datos.", 3)
    y_top = 11.5 * cm
    box(c, 1.2 * cm, y_top, 4.3 * cm, 1.8 * cm, "Usuario / Postman", "", LIGHT_BLUE, BLUE)
    box(c, 7.2 * cm, y_top, 4.8 * cm, 1.8 * cm, "api.gateway", "Puerto 8030", LIGHT_PURPLE, PURPLE)
    arrow(c, 5.5 * cm, y_top + 0.9 * cm, 7.2 * cm, y_top + 0.9 * cm, "Bearer Token")

    services = [
        ("ms.auth", "Login y token<br/>Puerto 8084", 2.0 * cm, 7.3 * cm, LIGHT_GREEN, GREEN),
        ("ms.cliente", "Clientes<br/>Puerto 8082", 8.1 * cm, 7.3 * cm, LIGHT_GREEN, GREEN),
        ("ms.producto", "Productos y stock<br/>Puerto 8080", 14.2 * cm, 7.3 * cm, LIGHT_GREEN, GREEN),
        ("ms.ventas", "Ventas + Saga<br/>Puerto 8083", 20.3 * cm, 7.3 * cm, LIGHT_GREEN, GREEN),
    ]
    for title, body, x, y, fill, stroke in services:
        box(c, x, y, 4.6 * cm, 1.9 * cm, title, body, fill, stroke)
        arrow(c, 9.6 * cm, y_top, x + 2.3 * cm, y + 1.9 * cm, "")

    dbs = [
        ("BD Auth", "", 2.0 * cm, 3.8 * cm),
        ("MySQL", "BD Cliente", 8.1 * cm, 3.8 * cm),
        ("Oracle", "BD Producto", 14.2 * cm, 3.8 * cm),
        ("PostgreSQL", "BD Ventas", 20.3 * cm, 3.8 * cm),
    ]
    for title, body, x, y in dbs:
        box(c, x, y, 4.6 * cm, 1.6 * cm, title, body, LIGHT_ORANGE, ORANGE)
        arrow(c, x + 2.3 * cm, 7.3 * cm, x + 2.3 * cm, y + 1.6 * cm, "persistencia")

    box(c, 2.0 * cm, 1.5 * cm, 10.5 * cm, 1.3 * cm, "Consul", "Descubrimiento y soporte de configuracion", LIGHT_GRAY, GRAY)
    box(c, 14.2 * cm, 1.5 * cm, 10.7 * cm, 1.3 * cm, "central-config", "Configuracion centralizada", LIGHT_GRAY, GRAY)
    arrow(c, 22.6 * cm, 7.3 * cm, 16.5 * cm, 9.2 * cm, "REST Client")
    arrow(c, 22.6 * cm, 7.3 * cm, 10.4 * cm, 9.2 * cm, "REST Client")
    c.showPage()


def page_c3_ventas(c):
    draw_header(c, "C3 - Componentes de ms.ventas", "Detalle del microservicio que orquesta la Saga.", 4)
    box(c, 2.0 * cm, 11.2 * cm, 5.0 * cm, 1.6 * cm, "api.gateway", "POST /api/ventas/saga", LIGHT_PURPLE, PURPLE)
    box(c, 9.0 * cm, 11.2 * cm, 5.0 * cm, 1.6 * cm, "VentaResource", "Controlador REST", LIGHT_GREEN, GREEN)
    box(c, 16.0 * cm, 10.7 * cm, 6.0 * cm, 2.4 * cm, "VentaServiceImpl", "Orquestador Saga<br/>logs, errores y compensacion", LIGHT_GREEN, GREEN)
    arrow(c, 7.0 * cm, 12.0 * cm, 9.0 * cm, 12.0 * cm)
    arrow(c, 14.0 * cm, 12.0 * cm, 16.0 * cm, 12.0 * cm)

    box(c, 4.0 * cm, 6.9 * cm, 5.0 * cm, 1.8 * cm, "ClienteClient", "REST Client Quarkus", LIGHT_BLUE, BLUE)
    box(c, 11.5 * cm, 6.9 * cm, 5.0 * cm, 1.8 * cm, "ProductoClient", "REST Client Quarkus", LIGHT_BLUE, BLUE)
    box(c, 19.0 * cm, 6.9 * cm, 5.0 * cm, 1.8 * cm, "VentaRepository", "Persistencia", LIGHT_GREEN, GREEN)
    arrow(c, 18.0 * cm, 10.7 * cm, 6.5 * cm, 8.7 * cm, "validar cliente")
    arrow(c, 19.0 * cm, 10.7 * cm, 14.0 * cm, 8.7 * cm, "stock")
    arrow(c, 21.0 * cm, 10.7 * cm, 21.5 * cm, 8.7 * cm, "guardar")

    box(c, 4.0 * cm, 3.7 * cm, 5.0 * cm, 1.6 * cm, "ms.cliente", "Valida cliente activo", LIGHT_GREEN, GREEN)
    box(c, 11.5 * cm, 3.7 * cm, 5.0 * cm, 1.6 * cm, "ms.producto", "Valida, descuenta y restaura stock", LIGHT_GREEN, GREEN)
    box(c, 19.0 * cm, 3.7 * cm, 5.0 * cm, 1.6 * cm, "PostgreSQL", "Tabla venta", LIGHT_ORANGE, ORANGE)
    arrow(c, 6.5 * cm, 6.9 * cm, 6.5 * cm, 5.3 * cm)
    arrow(c, 14.0 * cm, 6.9 * cm, 14.0 * cm, 5.3 * cm)
    arrow(c, 21.5 * cm, 6.9 * cm, 21.5 * cm, 5.3 * cm)
    c.showPage()


def page_c3_producto(c):
    draw_header(c, "C3 - Componentes de ms.producto", "Componentes usados por la Saga para controlar el stock.", 5)
    box(c, 2.0 * cm, 9.8 * cm, 5.0 * cm, 2.0 * cm, "ms.ventas", "Orquestador Saga", LIGHT_BLUE, BLUE)
    box(c, 10.0 * cm, 10.0 * cm, 5.5 * cm, 1.7 * cm, "ProductoResource", "Endpoints de stock", LIGHT_GREEN, GREEN)
    box(c, 18.0 * cm, 10.0 * cm, 5.5 * cm, 1.7 * cm, "ProductoService", "Reglas de negocio", LIGHT_GREEN, GREEN)
    box(c, 18.0 * cm, 6.6 * cm, 5.5 * cm, 1.7 * cm, "ProductoRepository", "Persistencia", LIGHT_GREEN, GREEN)
    box(c, 18.0 * cm, 3.2 * cm, 5.5 * cm, 1.7 * cm, "Oracle", "Tabla producto", LIGHT_ORANGE, ORANGE)
    arrow(c, 7.0 * cm, 10.8 * cm, 10.0 * cm, 10.8 * cm, "validar / descontar / restaurar")
    arrow(c, 15.5 * cm, 10.8 * cm, 18.0 * cm, 10.8 * cm)
    arrow(c, 20.75 * cm, 10.0 * cm, 20.75 * cm, 8.3 * cm)
    arrow(c, 20.75 * cm, 6.6 * cm, 20.75 * cm, 4.9 * cm)
    c.showPage()


def sequence_page(c, title, subtitle, page, compensation=False):
    draw_header(c, title, subtitle, page)
    actors = [
        ("Usuario", 2.0 * cm),
        ("Gateway", 6.5 * cm),
        ("ms.ventas", 11.0 * cm),
        ("ms.cliente", 15.5 * cm),
        ("ms.producto", 20.0 * cm),
        ("DB Ventas", 24.5 * cm),
    ]
    top = 12.2 * cm
    bottom = 2.0 * cm
    for name, x in actors:
        box(c, x - 1.35 * cm, top, 2.7 * cm, 0.9 * cm, name, "", LIGHT_GRAY, GRAY)
        c.setStrokeColor(colors.HexColor("#cbd5e1"))
        c.setDash(3, 3)
        c.line(x, top, x, bottom)
        c.setDash()

    steps = [
        (0, 1, "POST /api/ventas/saga"),
        (1, 2, "redirige solicitud"),
        (2, 3, "validar cliente"),
        (3, 2, "cliente activo"),
        (2, 4, "validar producto y stock"),
        (4, 2, "stock disponible"),
        (2, 4, "descontar stock"),
        (4, 2, "stock descontado"),
        (2, 5, "registrar venta"),
    ]
    if compensation:
        steps += [
            (5, 2, "error al guardar"),
            (2, 4, "restaurar stock"),
            (4, 2, "stock restaurado"),
            (2, 1, "Saga fallida compensada"),
            (1, 0, "409 controlado"),
        ]
    else:
        steps += [
            (5, 2, "venta registrada"),
            (2, 1, "Saga completada"),
            (1, 0, "respuesta OK"),
        ]

    y = 11.2 * cm
    for i, (a, b, label) in enumerate(steps, 1):
        x1 = actors[a][1]
        x2 = actors[b][1]
        arrow(c, x1, y, x2, y, f"{i}. {label}", ORANGE if compensation and "restaur" in label else GRAY)
        y -= 0.66 * cm
    c.showPage()


def summary(c):
    draw_header(c, "Resumen Arquitectonico", "Puntos clave de la solucion implementada.", 8)
    items = [
        ("Quarkus se mantiene", "No se reemplaza el framework ni la arquitectura existente."),
        ("Saga Orchestration", "ms.ventas coordina el flujo de venta y decide la compensacion."),
        ("REST Client", "La comunicacion interna se realiza usando clientes REST de Quarkus."),
        ("Gateway y token", "El usuario consume rutas oficiales por api.gateway con Bearer Token."),
        ("Consistencia", "Si falla despues de descontar stock, se restaura el stock descontado."),
    ]
    y = 11.2 * cm
    for title, body in items:
        box(c, 3.0 * cm, y, 22.0 * cm, 1.3 * cm, title, body, LIGHT_GREEN, GREEN)
        y -= 1.7 * cm
    c.showPage()


def main():
    OUT.parent.mkdir(parents=True, exist_ok=True)
    c = canvas.Canvas(str(OUT), pagesize=landscape(A4))
    c.setTitle("Diagramas C4 - Proyecto de Microservicios con Saga")
    c.setAuthor("Proyecto Quarkus")
    cover(c)
    page_c1(c)
    page_c2(c)
    page_c3_ventas(c)
    page_c3_producto(c)
    sequence_page(c, "Flujo Saga - Venta Exitosa", "Orden de llamadas cuando la venta se completa correctamente.", 6)
    sequence_page(c, "Flujo Saga - Compensacion", "Accion compensatoria cuando falla despues de descontar stock.", 7, compensation=True)
    summary(c)
    c.save()
    print(OUT)


if __name__ == "__main__":
    main()
