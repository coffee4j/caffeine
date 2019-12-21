-- Model

CREATE TABLE IF NOT EXISTS model
(
    id         INT IDENTITY PRIMARY KEY,
    name       VARCHAR(1024) NOT NULL,
    parameters ARRAY,
    UNIQUE (name)
);
CREATE INDEX idx_model_name on model (name);

CREATE TABLE IF NOT EXISTS forbidden_combination
(
    model_id    INT,
    name        VARCHAR(64),
    combination ARRAY,
    PRIMARY KEY (model_id, name),
    FOREIGN KEY (model_id) REFERENCES model (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS scenario
(
    model_id          INT,
    name              VARCHAR(64),
    strength          INT NOT NULL,
    faults            ARRAY,
    model_constraints ARRAY,
    PRIMARY KEY (model_id, name),
    FOREIGN KEY (model_id) REFERENCES model (id) ON DELETE CASCADE
);

-- Trace

CREATE TABLE IF NOT EXISTS trace
(
    id             INT IDENTITY PRIMARY KEY,
    model_id       INT,
    scenario_name  VARCHAR(64),
    algorithm_name VARCHAR(1024),
    UNIQUE (model_id, scenario_name, algorithm_name),
    FOREIGN KEY (model_id) REFERENCES model (id) ON DELETE CASCADE,
    FOREIGN KEY (model_id, scenario_name) REFERENCES scenario (model_id, name) ON DELETE CASCADE
);
CREATE INDEX idx_trace_algorithm ON trace (algorithm_name);

CREATE TABLE IF NOT EXISTS trace_iteration
(
    trace_id  INT,
    iteration INT,
    state     varchar(20),
    time      INT,
    PRIMARY KEY (trace_id, iteration),
    FOREIGN KEY (trace_id) REFERENCES trace (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS test_case
(
    trace_id    INT,
    iteration   INT,
    sort_index  INT,
    combination ARRAY,
    PRIMARY KEY (trace_id, iteration, sort_index),
    FOREIGN KEY (trace_id) REFERENCES trace (id) ON DELETE CASCADE,
    FOREIGN KEY (trace_id, iteration) REFERENCES trace_iteration (trace_id, iteration) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS failure_inducing_combination
(
    trace_id    INT,
    iteration   INT,
    sort_index  INT,
    combination ARRAY,
    PRIMARY KEY (trace_id, iteration, sort_index),
    FOREIGN KEY (trace_id) REFERENCES trace (id) ON DELETE CASCADE,
    FOREIGN KEY (trace_id, iteration) REFERENCES trace_iteration (trace_id, iteration) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS assumption
(
    trace_id       INT,
    iteration      INT,
    assumption_key VARCHAR(1024),
    satisfied      BOOLEAN NOT NULL,
    PRIMARY KEY (trace_id, iteration, assumption_key),
    FOREIGN KEY (trace_id) REFERENCES trace (id) ON DELETE CASCADE,
    FOREIGN KEY (trace_id, iteration) REFERENCES trace_iteration (trace_id, iteration) ON DELETE CASCADE
);


-- Analysis

CREATE TABLE IF NOT EXISTS model_analysis
(
    id       INT IDENTITY PRIMARY KEY,
    model_id INT,
    FOREIGN KEY (model_id) REFERENCES model (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS scenario_analysis
(
    id            INT IDENTITY PRIMARY KEY,
    model_id      INT,
    scenario_name VARCHAR(64),
    FOREIGN KEY (model_id) REFERENCES model (id) ON DELETE CASCADE,
    FOREIGN KEY (model_id, scenario_name) REFERENCES scenario (model_id, name) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trace_analysis
(
    id        INT IDENTITY PRIMARY KEY,
    trace_id  INT,
    iteration INT,
    FOREIGN KEY (trace_id) REFERENCES trace (id) ON DELETE CASCADE,
    FOREIGN KEY (trace_id, iteration) REFERENCES trace_iteration (trace_id, iteration) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS property_key
(
    id        INT IDENTITY PRIMARY KEY,
    prop_key  VARCHAR(128),
    data_type VARCHAR(1024),
    min       DOUBLE,
    max       DOUBLE
);
CREATE INDEX idx_prop_key ON property_key (prop_key);

CREATE TABLE IF NOT EXISTS model_properties
(
    analysis_id INT,
    prop_key    INT,
    value       DOUBLE,
    PRIMARY KEY (analysis_id, prop_key),
    FOREIGN KEY (analysis_id) REFERENCES model_analysis (id) ON DELETE CASCADE,
    FOREIGN KEY (prop_key) REFERENCES property_key (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS scenario_properties
(
    analysis_id INT,
    prop_key    INT,
    value       DOUBLE,
    PRIMARY KEY (analysis_id, prop_key),
    FOREIGN KEY (analysis_id) REFERENCES scenario_analysis (id) ON DELETE CASCADE,
    FOREIGN KEY (prop_key) REFERENCES property_key (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trace_properties
(
    analysis_id INT,
    prop_key    INT,
    value       DOUBLE,
    PRIMARY KEY (analysis_id, prop_key),
    FOREIGN KEY (analysis_id) REFERENCES trace_analysis (id) ON DELETE CASCADE,
    FOREIGN KEY (prop_key) REFERENCES property_key (id) ON DELETE CASCADE
);




