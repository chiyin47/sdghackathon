import React, { useState } from 'react';
import MapComponent from './MapComponent';
import AutocompleteInput from './AutocompleteInput';
import EnvironmentalImpactDisplay from './EnvironmentalImpactDisplay'; // Import the new component

function GreenRouteDemo() {
  const [origin, setOrigin] = useState('');
  const [destination, setDestination] = useState('');
  const [stops, setStops] = useState(['']);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [mapCenterLatitude, setMapCenterLatitude] = useState(34.0522); // Default to a central point
  const [mapCenterLongitude, setMapCenterLongitude] = useState(-118.2437); // Default to a central point

  const handleStopChange = (index, value) => {
    const newStops = [...stops];
    newStops[index] = value;
    setStops(newStops);
  };

  const addStop = () => {
    setStops([...stops, '']);
  };

  const removeStop = (index) => {
    const newStops = stops.filter((_, i) => i !== index);
    setStops(newStops);
  };

  const handleReverse = () => {
    const temp = origin;
    setOrigin(destination);
    setDestination(temp);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleClick();
    }
  };

  const handleClick = () => {
    setLoading(true);
    setResult(null);
    setError('');

    const waypoints = stops.filter(stop => stop.trim() !== '').join('|');
    let url = `http://localhost:8080/route?origin=${origin}&destination=${destination}`;
    if (waypoints) {
      url += `&waypoints=${waypoints}`;
    }

    fetch(url)
      .then(res => res.json())
      .then(data => {
        if (data.content.startsWith('Error') || data.content.startsWith('No routes')) {
          setError(data.content);
          setResult(null);
        } else {
          setResult(data);
        }
      })
      .catch(() => setError('Error fetching data. Is the backend running?'))
      .finally(() => setLoading(false));
  };

  const handleMapMove = (lat, lng) => {
    setMapCenterLatitude(lat);
    setMapCenterLongitude(lng);
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Green Route Demo</h2>
      <p>Find the shortest route by distance to save fuel.</p>

      <div>
        <label>Origin:</label>
        <AutocompleteInput value={origin} onChange={setOrigin} onKeyDown={handleKeyPress} placeholder="e.g., Kuala Lumpur"/>
      </div>

      <button onClick={handleReverse} style={{ margin: '10px 0' }}>Reverse</button>

      <div>
        <label>Destination:</label>
        <AutocompleteInput value={destination} onChange={setDestination} onKeyDown={handleKeyPress} placeholder="e.g., Johor Bahru"/>
      </div>

      <div>
        <label>Stops:</label>
        {stops.map((stop, index) => (
          <div key={index} style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
            <div style={{ flex: 1 }}>
              <AutocompleteInput 
                value={stop} 
                onChange={value => handleStopChange(index, value)} 
                onKeyDown={handleKeyPress} 
                placeholder={`Stop ${index + 1}`}
              />
            </div>
            <button onClick={() => removeStop(index)} style={{ marginLeft: '10px' }}>Remove</button>
          </div>
        ))}
        <button onClick={addStop} style={{ marginTop: '10px' }}>Add Stop</button>
      </div>

      <button onClick={handleClick} disabled={loading} style={{ marginTop: '20px' }}>
        {loading ? 'Finding Route...' : 'Get Green Route'}
      </button>

      {error && <div style={{ color: 'red', marginTop: '10px' }}>{error}</div>}

      <div style={{ height: '500px', width: '100%', marginTop: '20px' }}>
        <MapComponent route={result} onMapMove={handleMapMove} />
      </div>

      {result && (
        <div style={{ marginTop: '20px' }}>
          <h3>Best Route Found:</h3>
          <p><strong>Route:</strong> {result.content}</p>
          <p><strong>Distance:</strong> {result.distance}</p>
          <p><strong>Duration:</strong> {result.duration}</p>
          <p><strong>Fuel Used:</strong> {result.fuelUsed}</p>
        </div>
      )}

      <EnvironmentalImpactDisplay
        latitude={mapCenterLatitude}
        longitude={mapCenterLongitude}
        distance={result ? result.distance : 0} // Use actual distance from result
        transportationMode={"car"} // Placeholder, ideally this would be user selected or derived
      />
    </div>
  );
}

export default GreenRouteDemo;

