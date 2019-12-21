#!/usr/bin/env python
""""
Usage: visualization.py

This script performs the task of visualizing the results of the benchmark infrastructure.

The requirements are listed in the requirements.txt
To import them run (preferably in a new virtualenv):   pip -r requirements.txt

To start the visualization:     python visualization.py analysis.csv
Subsequently, the visualization can be accessed at: http://127.0.0.1:8050/

For easier testing a viz-config.json as well as an example analysis.csv are provided in this folder.
"""

# Dash
from collections import namedtuple

import dash
import dash_core_components as dcc
import dash_html_components as html
import dash_table
from dash.dependencies import Input, Output

# Data wrangling and filtering
import numpy as np
import pandas as pd

# Miscellaneous
import json
import sys

BaselineLabel = namedtuple('BaselineLabel', ['column', 'label'])
Filter = namedtuple("Filter", "column id")


def layout(filters):
    """Create the page layout."""
    return html.Div(children=[
        # Error Message
        html.Div(id="error-message"),
        # Top Banner
        html.Div(
            className="banner row",
            children=[
                html.H2(className="h2-title-mobile", children="EVALUATION"),
                html.H2(className="h2-title", children="EVALUATION"),
                html.Div(
                    className="div-logo",
                    children=html.Img(
                        className="logo", src=app.get_asset_url("coffee.png")
                    ),
                )
            ],
        ),
        # Body of the App
        html.Div(
            className="row app-body",
            children=[
                # User Controls
                html.Div(
                    className="four columns card",
                    children=[
                        html.Div(
                            className="bg-white user-control",
                            children=[
                                html.Div(
                                    className="padding-top-bot",
                                    children=[
                                                 html.Div(id="states"),
                                                 html.H4("Filters: ")
                                             ] + filters,
                                )
                            ],
                        )
                    ],
                ),
                # Graph
                html.Div(
                    className="eight columns card-left",
                    children=[
                        html.Div(
                            className="bg-white",
                            children=[
                                dcc.Tabs(id="tabs", children=[
                                    dcc.Tab(label='Bar', children=[
                                        html.Div([
                                            dcc.Dropdown(
                                                id="bar-dropdown",
                                                options=[{"label": get_display_name(metric), "value": metric} for metric
                                                         in data.iloc[:, 5:]],
                                                value=[],
                                                multi=True
                                            ),
                                            dcc.Graph(
                                                id='bar-graph',
                                                config={
                                                    "toImageButtonOptions": {
                                                        "format": "svg",
                                                        "width": 800,
                                                        "height": 600
                                                    }
                                                }
                                            )
                                        ])
                                    ]),
                                    dcc.Tab(label='Line', children=[
                                        html.Div([
                                            dcc.Dropdown(
                                                id='xaxis-column',
                                                options=[{'label': get_display_name(i), 'value': i} for i in
                                                         data.iloc[:, 5:]],
                                                clearable=False

                                            ),
                                            dcc.RadioItems(
                                                id='xaxis-type',
                                                options=[{'label': i, 'value': i} for i in ['Linear', 'Log']],
                                                value='Linear',
                                                labelStyle={'display': 'inline-block'}
                                            )
                                        ],
                                            style={'width': '48%', 'display': 'inline-block'}),

                                        html.Div([
                                            dcc.Dropdown(
                                                id='yaxis-column',
                                                options=[{'label': get_display_name(i), 'value': i} for i in
                                                         data.iloc[:, 5:]],
                                                clearable=False
                                            ),
                                            dcc.RadioItems(
                                                id='yaxis-type',
                                                options=[{'label': i, 'value': i} for i in ['Linear', 'Log']],
                                                value='Linear',
                                                labelStyle={'display': 'inline-block'}
                                            )
                                        ], style={'width': '48%', 'float': 'right', 'display': 'inline-block'}),
                                        dcc.Graph(
                                            id='line-graph',
                                            config={
                                                "toImageButtonOptions": {
                                                    "format": "svg",
                                                    "width": 800,
                                                    "height": 600
                                                }
                                            }
                                        )
                                    ])
                                ])
                            ]
                        )
                    ],
                )
            ]
        )
    ])


def load_data(path):
    """Load the data from the given CSV."""
    return pd.read_csv(path, skipinitialspace=True)


def load_config(path):
    """Load the visualization configuration file."""
    with open(path) as config_file:
        return json.load(config_file)


def update_bar_chart(metrics, *filters):
    """Update the bar chart panel with either new filters or new metrics."""
    agg_filter = aggregate_filters(filters)
    data_sets = []
    aggregate = data[agg_filter].groupby('Algorithm').mean()
    for value in metrics:
        data_sets.append({
            "type": "bar",
            "name": get_display_name(value),
            "x": aggregate.index.tolist(),
            "y": aggregate[value].tolist()
        })
    return {"data": data_sets,
            "layout": {
                "legend": {
                    "orientation": "h"
                },
                "font": {"size": 14}
            }}


def aggregate_filters(filters):
    """Aggregate all filter values for more efficient access in a dataframe."""
    return np.bitwise_and.reduce(
        [(data[column] <= domain[1]) & (data[column] >= domain[0]) for column, domain in
         zip(viz_config["filters"], filters)])


def update_states(*filters):
    """Update the state table with the new filters."""
    agg_filter = aggregate_filters(filters)
    tab = data[agg_filter].pivot_table(index='Algorithm', columns='State', aggfunc="size",
                                       fill_value=0).reset_index()
    return html.Div(
        dash_table.DataTable(columns=[{"name": i, "id": i} for i in tab.columns], data=tab.to_dict("records")))


def update_line_chart(x_metric, x_type, y_metric, y_type, *filters):
    """Update the line chart panel with either new filters or new metrics."""
    if x_metric is None or y_metric is None or x_metric == y_metric:
        return {"data": []}
    agg_filter = aggregate_filters(filters)
    data_sets = []
    groups = data[agg_filter].groupby(["Algorithm", x_metric])
    aggregate = groups.mean()[y_metric]
    for algorithm, inner in aggregate.groupby(level=0):
        data_sets.append({
            "type": "line",
            "name": algorithm,
            "x": inner.index.get_level_values(1).tolist(),
            "y": inner.tolist()
        })
    return {"data": data_sets,
            "layout": {
                "yaxis": {
                    "title": get_display_name(y_metric),
                    'type': 'linear' if y_type == 'Linear' else 'log'
                },
                "xaxis": {
                    "title": get_display_name(x_metric),
                    'type': 'linear' if x_type == 'Linear' else 'log'
                },
                "font": {"size": 14}
            }}


def get_display_name(column_id):
    """Gets the display name of a column from the configuration."""
    return viz_config["metrics"].get(column_id, {"label": column_id})["label"]


def register_callbacks(filters):
    """Register the Dash callbacks depending on the data read from the analysis file."""
    for column_filter in filters:
        app.callback(Output("label-" + column_filter.id, 'children'),
                     [Input(column_filter.id, 'value')])(update_slider_label(column_filter.column))
    app.callback(Output("bar-graph", 'figure'),
                 [Input("bar-dropdown", "value")] +
                 [Input(column_filter.id, "value") for column_filter in filters])(update_bar_chart)
    app.callback(Output("line-graph", 'figure'),
                 [Input("xaxis-column", "value"),
                  Input("xaxis-type", "value"),
                  Input("yaxis-column", "value"),
                  Input("yaxis-type", "value")] +
                 [Input(column_filter.id, "value") for column_filter in filters])(update_line_chart)
    app.callback(Output("states", "children"),
                 [Input(column_filter.id, "value") for column_filter in filters])(update_states)


def update_slider_label(column):
    """
    Update the slider labels to their update states.
    Ideally this would be solved directly in javascript because it is purely visual.
    """
    return lambda range_value: "{}: {}..{}".format(get_display_name(column), *range_value)


def add_baseline_columns(base_data, column_labels, algorithm):
    """
    Add the baselined columns to the given dataframe. They are calculated by dividing the real values with the
    respective values from the baseline column.
    """
    for column, label in column_labels:
        base_data[label] = np.NAN
    for index, row in base_data.iterrows():
        baselines = base_data[(base_data['Model'] == row['Model']) &
                              (base_data['Scenario'] == row['Scenario']) &
                              (base_data['Algorithm'] == algorithm)][[column for column, label in column_labels]]
        for idx, column_label in enumerate(column_labels):
            base_data.at[index, column_label.label] = row[column_label.column] / baselines[column_label.column]


def store_data(path, data):
    """Store the data to a CSV file."""
    data.to_csv(path, index=False)


def create_filters(data, filter_config):
    """Create the HTML and configuration for the filter panel."""
    filters = []
    filter_names = []
    for column in filter_config:
        min_value = data[column].min()
        max_value = data[column].max()
        filter_id = "filter-" + column
        filter_names.append(Filter(column, filter_id))
        filters.append(html.Div(className="filter-control",
                                children=[html.Div(id="label-" + filter_id),
                                          dcc.RangeSlider(id=filter_id, min=min_value, max=max_value,
                                                          value=(min_value, max_value), step=1)
                                          ]))
    return filters, filter_names


viz_config = load_config('viz-config.json')

if __name__ == '__main__':
    data_path = sys.argv[1]
    data = load_data(data_path)
    baseline_config = [BaselineLabel(column, values["label"])
                       for column, values in viz_config['baseline']['columns'].items()]
    add_baseline_columns(data, baseline_config, viz_config["baseline"]["algorithm"])
    filter_html, filter_config = create_filters(data, viz_config["filters"])
    app = dash.Dash(__name__, meta_tags=[
        {"name": "viewport", "content": "width=device-width, initial-scale=1"}
    ])
    app.layout = layout(filter_html)
    register_callbacks(filter_config)
    app.run_server()
