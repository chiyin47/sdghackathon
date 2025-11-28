import React, { useState, useEffect } from 'react';

function EnvironmentalImpactDisplay({ latitude, longitude, distance, transportationMode }) {
    const [environmentalData, setEnvironmentalData] = useState(null);
    const [carbonEmissions, setCarbonEmissions] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (latitude && longitude) {
            fetch(`http://localhost:8080/environmental-data?latitude=${latitude}&longitude=${longitude}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => setEnvironmentalData(data))
                .catch(error => {
                    console.error("Error fetching environmental data:", error);
                    setError("Failed to fetch environmental data.");
                });
        }
    }, [latitude, longitude]);

    useEffect(() => {
        if (distance && transportationMode) {
            fetch(`http://localhost:8080/carbon-emissions?distance=${distance}&transportationMode=${transportationMode}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => setCarbonEmissions(data))
                .catch(error => {
                    console.error("Error fetching carbon emissions:", error);
                    setError("Failed to fetch carbon emissions data.");
                });
        }
    }, [distance, transportationMode]);

    return (
        <div className="environmental-impact-display">
            <h2>Environmental Impact</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}

            {environmentalData ? (
                <div>
                    <h3>Environmental Data:</h3>
                    <p>Air Quality: {environmentalData.airQuality}</p>
                    <p>Temperature: {environmentalData.temperature}</p>
                    <p>Humidity: {environmentalData.humidity}</p>
                </div>
            ) : (
                <p>Loading environmental data...</p>
            )}

            {carbonEmissions !== null ? (
                <div>
                    <h3>Carbon Emissions:</h3>
                    <p>{carbonEmissions} kg CO2</p>
                </div>
            ) : (
                <p>Loading carbon emissions...</p>
            )}
        </div>
    );
}

export default EnvironmentalImpactDisplay;
