# HardwareHero - Site Environmental Auditor

A command-line utility for HardwareHero technicians that fetches live weather data for a client's location and produces a formatted  **Site Environmental Audit Report**, highlighting temperature and humidity risks to server room cooling systems.

---

##  Prerequisites
| Requirement     | Version |
|-----------------|---------|
| Java JDK        | 17 or later |
| Apache Maven    | 3.8 or later | 
| IntelliJ IDEA   | Any edition (Community or Ultimate) |
| Internet access | Required to reach `api.openweathermap.org` |

---

##  Getting an OpenWeatherMap API Key

1. Go to [https://openweathermap.org/](https://openweathermap.org/) and create a free account.
2. after logging in, navigate to **My Profile -> API Keys**.
3. Copy the default key (or generate a new one).

> **Note:** Free-tier keys may take up to 2 hours to activate after creation.
> The free tier allows up tp **60 API calls per minute**, which is more than sufficient for routine audit use.

---

## Setting the API key as an Environment Variable
The tool reads the API key from the `OWM_API_KEY` environment variable at runtime.
**Never hard-code or commit your API key.**

### macOS / Linux

```bash
# Temporary (current terminal session only)
export OWM_API_KEY="you_api_key"

# Permanent (add to ~/.bashrc, ~/.zshrc, or equivalent)
echo 'export OWM_API_KEY="you_api_key"' >> ~/.zshrc
source ~/.zshrc
```

### Windows (Command Prompt)
```cmd
:: Temporary
set OWM_API_KEY=your_api_key

:: Permanent
setx OWM_API_KEY=your_api_key
```

### Windows (PowerShell)
```powershell
# Temporary
$env:OWM_API_KEY=your_api_key

# Permanent
[System.Environment]::SetEnvironmentVariable("OWM_API_KEY", "your_api_key", "User")
```

### IntelliJ IDEA Run Configuration
1. Open Run -> Edit Configuration
2. Select the `App` run configuration (or create one).
3. Click **Edit environment variable**.
4. Add `OWM_API_KEY` as name and `your_api_key` as value.
5. Click **OK** and run as normal.

---

## Building the Project

### Option A - IntelliJ IDEA
1. Open IntelliJ IDEA and choose **File -> Open**, then select the project folder.
2. IntelliJ will detect the `pom.xml` and import the Maven project automatically.
3. Wait for the dependency download to finish.
4. Open the **Maven** panel (View -> Tool Windows -> Maven) and double click:
   ```
   hardware-hero -> Lifecycle -> package
   ```
   This produces a self-contained JAR at `target/hardware-hero-1.0.0.jar`.

### Option B - Maven command line
```bash
cd hardware-hero
mvn clean package
```

---

## Running the Tool
After building, run the fat JAR from your terminal.

### With a location argument
```bash
java -jar target/hardware-hero-1.0.0.jar "Jakarta,ID"
java -jar target/hardware-hero-1.0.0.jar "London,GB"
java -jar target/hardware-hero-1.0.0.jar "10001,US"
```

### Interactive mode
```bash
java -jar target/hardware-hero-1.0.0.jar
# Prompt: Enter city name or ZIP code ...
```

---

## Sample Output
```
======================================================================
                             HARDWAREHERO
                   Site Environmental Audit Report
======================================================================
  Location  : Jakarta, ID
  Conditions: Scattered clouds
  Generated : 2026-03-19 13:35:01 WIB
----------------------------------------------------------------------
                           CURRENT READINGS

  Temperature:           33.7 °C (feels like 37.7 °C)
  Relative Humidity:     50 %
----------------------------------------------------------------------
 OVERALL STATUS: ADVISORY - Minor concerns detected; monitor closely.
----------------------------------------------------------------------
                            RISK FINDINGS

  [ ADV ] Temperature
     Reading : 33.7 °C - Warm conditions
     Action  : Temperatures are warmer than ideal. Monitor cooling
             unit performance and confirm CRAC/CRAH setpoints have
             not drifted.

  [ OK  ] Humidity
     Reading : 50 % - Within normal range
     Action  : No humidity-related action required at this time.

----------------------------------------------------------------------
  This report was generated automatically by HardwareHero.
  Weather data provided by OpenWeatherMap (openweathermap.org).
  For urgent concerns, contact your HardwareHero technician.
======================================================================
```

---

## Risk Assessment Logic

The risk engine (`RiskAssessor.java`) evaluates two independent dimensions:

### Temperature
| Reading | Level | Meaning |
|---------|-------|---------|
| < 30 °C | OK | Normal operating conditions |
| 30-34 °C| ADVISORY | Warm; monitor cooling unit performance |
| 35-39 °C| WARNING | Heatwave; verify cooling headroom |
| >= 40 °C| CRITICAL | Extreme heat; reduce server load immediately |

### Relative Humidity
| Reading | Level | Meaning                            |
|---------|-------|------------------------------------|
| < 60 %  | OK | Normal operating conditions        |
| 60-69 % | ADVISORY | Elevated; watch for HVAC drift     |
| 70-84 % | WARNING | Condensation risk on cold surfaces |
| >= 85 % | CRITICAL | High corrosion / condensation risk |

The Overall Status reflects the single highest level across all findings.

To update thresholds, edit the constants at the top of `RiskAssessor.java` - no other files need to change.

---