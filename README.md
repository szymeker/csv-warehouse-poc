# Simple CSV warehouse

## About
This project is a proof-of-concept of simple csv warehouse for demo purposes.

It allows loading a csv file and then querying it using provided API.


## Local build
Build application:
```
mvn clean install
```

Build image:
```
docker build -t csvwarehouse .
```

Run:
```
docker run -d -p 8080:8080 csvwarehouse
```

## API reference
### Loading data

#### Parameters
| Parameter         | Type         | Description                                            | Example value                 |
|-------------------|--------------|--------------------------------------------------------|-------------------------------|
| fileUrl           | String       | CSV file to be loaded                                  | `http://example.com/file.csv` |
| timeColumn        | String       | Time column name                                       | `Daily`                       |
| timeFormat        | String       | Time column datetime format                            | `M/d/yy`                      |
| dimensionColumns  | List<String> | List of columns to be used as dimensions               | `Datasource,Campaign`         |
| metricColumns     | List<String> | List of columns to be used as metrics                  | `Impressions,Clicks`          |
| metricColumnTypes | List<String> | Types of metric column, 1:1 with `metricColumns` param | `LONG,LONG`                   |

#### Usage example
Load data into application:
```
curl --location --request POST 'http://localhost:8080/loadDataFile?fileUrl={YOUR_FILE_URL}&timeColumn=Daily&dimensionColumns=Datasource,Campaign&metricColumns=Impressions,Clicks&metricColumnTypes=LONG,LONG&timeFormat=M/d/yy'
```

Sample response:
```
200 OK Loaded
```

### Querying data

#### Parameters
| Parameter     | Type          | Required | Description                                                             |                          |
|---------------|---------------|----------|-------------------------------------------------------------------------|--------------------------|
| metrics       | Set<String>   | false    | Metric names to be present in the response                              | `Clicks,Impressions,CTR` |
| filters       | List<String>  | false    | Filters with format: column1=value1,column2=value2                      | `Datasource=Google Ads`  |
| startDateTime | ISO date time | false    | Time range start (inclusive)                                            | `2010-09-16T08:00:00`    |
| endDateTime   | ISO date time | false    | Time range end (exclusive)                                              | `2020-09-16T08:00:00`    |
| groupColumns  | Set<String>   | false    | Columns to group by your data                                           | `Datasource`             |
| groupByTime   | Boolean       | false    | If true then specific time bucket will be created for each group column | `false`                  |

### Sample requests/response
#### Total Clicks for a given Datasource for a given Date range
Query:
```
curl --location --request GET 'http://localhost:8080/query?startDateTime=2019-11-10T00:00:00&endDateTime=2019-11-15T00:00:00&metrics=Clicks&Datasource=Google Ads'
```
Response:
```
[
    {
        "timeDimension": null,
        "dimensions": {},
        "metrics": {
            "Clicks": 47025
        }
    }
]
```
#### Click-Through Rate (CTR) per Datasource and Campaign
Query:
```
curl --location --request GET 'http://localhost:8080/query?groupColumns=Datasource,Campaign&metrics=CTR'
```

Response:
```
[
    {
        "timeDimension": null,
        "dimensions": {
            "Datasource": "Google Ads",
            "Campaign": "Adventmarkt Touristik"
        },
        "metrics": {
            "CTR": 0.002920055781411923
        }
    },
    {
        "timeDimension": null,
        "dimensions": {
            "Datasource": "Google Ads",
            "Campaign": "Firmen Mitgliedschaft"
        },
        "metrics": {
            "CTR": 0.0
        }
    },
    {
        "timeDimension": null,
        "dimensions": {
            "Datasource": "Google Ads",
            "Campaign": "GDN_Retargeting"
        },
        "metrics": {
            "CTR": 0.0012689784013030986
        }
    },
    {
        "timeDimension": null,
        "dimensions": {
            "Datasource": "Google Ads",
            "Campaign": "Mitgliedschaft KiMi"
        },
        "metrics": {
            "CTR": 0.0
        }
    },
    {
        "timeDimension": null,
        "dimensions": {
            "Datasource": "Google Ads",
            "Campaign": "Motorrad Mitgliedschaft"
        },
        "metrics": {
            "CTR": 6.518855553498074E-5
        }
    },
    
    // omitted for readability
]
```
#### Impressions over time (daily)
Query:
```
curl --location --request GET 'http://localhost:8080/query?groupByTime=true&metrics=Impressions'
```

Response:
```
[
    {
        "timeDimension": "2019-11-12T00:00:00",
        "dimensions": {},
        "metrics": {
            "Impressions": 275228
        }
    },
    {
        "timeDimension": "2019-11-13T00:00:00",
        "dimensions": {},
        "metrics": {
            "Impressions": 232068
        }
    },
    {
        "timeDimension": "2019-11-14T00:00:00",
        "dimensions": {},
        "metrics": {
            "Impressions": 262695
        }
    },
    {
        "timeDimension": "2019-11-15T00:00:00",
        "dimensions": {},
        "metrics": {
            "Impressions": 232540
        }
    },
    {
        "timeDimension": "2019-11-16T00:00:00",
        "dimensions": {},
        "metrics": {
            "Impressions": 186813
        }
    },
    
    // omitted for readability
]
```