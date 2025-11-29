import React, { useState, useMemo } from 'react';

const debounce = (func, delay) => {
    let timeoutId;
    return (...args) => {
        if (timeoutId) {
            clearTimeout(timeoutId);
        }
        timeoutId = setTimeout(() => {
            func(...args);
        }, delay);
    };
};

const AutocompleteInput = ({ value, onChange, onKeyDown, placeholder }) => {
    const [suggestions, setSuggestions] = useState([]);

    const fetchSuggestions = (inputValue) => {
        if (!inputValue) {
            setSuggestions([]);
            return;
        }
        fetch(`https://sdghackathon-1-173683223586.asia-southeast1.run.app/places/autocomplete?input=${inputValue}`)
            .then(res => res.json())
            .then(data => {
                setSuggestions(data);
            })
            .catch(err => {
                console.error("Error fetching suggestions:", err);
                setSuggestions([]);
            });
    };

    const debouncedFetchSuggestions = useMemo(() => debounce(fetchSuggestions, 300), []);

    const handleChange = (e) => {
        const newValue = e.target.value;
        onChange(newValue);
        debouncedFetchSuggestions(newValue);
    };

    const onSuggestionClick = (suggestion) => {
        onChange(suggestion);
        setSuggestions([]);
    };

    return (
        <div style={{ position: 'relative' }}>
            <input
                value={value}
                onChange={handleChange}
                onKeyDown={onKeyDown}
                placeholder={placeholder}
                style={{ width: '100%' }}
            />
            {suggestions.length > 0 && (
                <ul style={{
                    position: 'absolute',
                    top: '100%',
                    left: 0,
                    right: 0,
                    backgroundColor: 'white',
                    border: '1px solid #ccc',
                    listStyle: 'none',
                    margin: 0,
                    padding: 0,
                    zIndex: 1000,
                }}>
                    {suggestions.map((suggestion, index) => (
                        <li
                            key={index}
                            onClick={() => onSuggestionClick(suggestion)}
                            style={{ padding: '8px', cursor: 'pointer' }}
                        >
                            {suggestion}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default AutocompleteInput;
