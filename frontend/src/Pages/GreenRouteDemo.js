import React, { useState } from 'react';
import MapComponent from './MapComponent';

function GreenRouteDemo() {
  const [origin, setOrigin] = useState('');
  const [destination, setDestination] = useState('');
  const [stops, setStops] = useState(['']);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

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

  return (
    <div style={{ padding: '20px' }}>
      <h2>Green Route Demo</h2>
      <p>Find the shortest route by distance to save fuel.</p>

      <div>
        <label>Origin:</label>
        <input value={origin} onChange={e => setOrigin(e.target.value)} placeholder="e.g., Kuala Lumpur"/>
      </div>

      <button onClick={handleReverse} style={{ margin: '10px 0' }}>Reverse</button>

      <div>
        <label>Destination:</label>
        <input value={destination} onChange={e => setDestination(e.target.value)} placeholder="e.g., Johor Bahru"/>
      </div>

      <div>
        <label>Stops:</label>
        {stops.map((stop, index) => (
          <div key={index}>
            <input value={stop} onChange={e => handleStopChange(index, e.target.value)} placeholder={`Stop ${index + 1}`}/>
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
        <MapComponent route={result} />
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
    </div>
  );
}

export default GreenRouteDemo;
