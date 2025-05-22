from flask import Flask, render_template
import plotly.graph_objs as go
import plotly.offline as pyo

app = Flask(__name__)

@app.route('/')
def index():
    # Datos de ejemplo
    meses = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo']
    compras = [20000, 25000, 22000, 28000, 30000]
    gastos = [15000, 18000, 16000, 17000, 19000]
    ganancias = [c - g for c, g in zip(compras, gastos)]

    # Crear gráfica
    fig = go.Figure()
    fig.add_trace(go.Bar(x=meses, y=compras, name='Compras', marker_color='blue'))
    fig.add_trace(go.Bar(x=meses, y=gastos, name='Gastos', marker_color='red'))
    fig.add_trace(go.Bar(x=meses, y=ganancias, name='Ganancias', marker_color='green'))
    fig.update_layout(title='Finanzas de Volkswagen', barmode='group')

    # Convertir a HTML
    plot_div = pyo.plot(fig, output_type='div', include_plotlyjs=True)

    return render_template('dashboard.html', plot_div=plot_div)
