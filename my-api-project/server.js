const express = require('express');
const sqlite3 = require('sqlite3').verbose();
const xlsx = require('xlsx'); // Import xlsx library
const fs = require('fs'); // To save the Excel file temporarily
const app = express();
app.use(express.json()); // To parse JSON bodies

// Open SQLite database
const db = new sqlite3.Database('UserDatabase.db', (err) => {
    if (err) {
        console.error('Error opening database:', err.message);
    } else {
        console.log('Connected to the SQLite database.');
    }
});

// POST endpoint to analyze data and export to Excel
app.post('/analyze', (req, res) => {
    const data = req.body.data; // Get data from the request body

    // Query the database to fetch all products
    db.all("SELECT * FROM products", [], (err, rows) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }

        // Create a new workbook
        const wb = xlsx.utils.book_new();
        
        // Convert rows (array of product objects) to a worksheet
        const ws = xlsx.utils.json_to_sheet(rows);
        
        // Append worksheet to workbook
        xlsx.utils.book_append_sheet(wb, ws, "Products");

        // Generate Excel file as a buffer
        const excelBuffer = xlsx.write(wb, { bookType: "xlsx", type: "buffer" });

        // Optionally, save the file temporarily for download
        const filePath = './products_analysis.xlsx';
        fs.writeFileSync(filePath, excelBuffer);

        // Send the Excel file as a response for download
        res.download(filePath, 'products_analysis.xlsx', (err) => {
            if (err) {
                console.error('Error sending file:', err);
                res.status(500).send("Error exporting the file");
            }

            // Optionally, delete the file after sending it
            fs.unlinkSync(filePath);
        });
    });
});

// Start the server
app.listen(3000, () => {
    console.log("Server is running on http://localhost:3000");
});
