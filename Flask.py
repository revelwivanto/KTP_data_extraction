from flask import Flask, request, jsonify, render_template, url_for, send_from_directory
from werkzeug.utils import secure_filename
import os

app = Flask(__name__)

# Define the upload directory
upload_dir = "C:\\Users\\EXA\\StudioProjects\\KTP_data_extraction-main\\signature_detection\\data\\input"

@app.route("/")
def showHomePage():
    # Get the list of uploaded image files
    image_files = os.listdir(upload_dir)
    # Render the homepage HTML template with the list of image files
    return render_template("index.html", image_files=image_files)

@app.route("/upload", methods=["POST"])
def upload():
    # Handle image upload here
    if 'file' not in request.files:
        return jsonify({"error": "No file part"})
    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"})
    # Save the uploaded file to the specified directory
    file_path = os.path.join(upload_dir, secure_filename(file.filename))
    file.save(file_path)
    # Return a response indicating success or failure
    return jsonify({"message": "Image uploaded successfully!"})

@app.route('/uploads/<filename>')
def uploaded_file(filename):
    return send_from_directory(upload_dir, filename)

if __name__ == "__main__":
    app.run(host="0.0.0.0")

