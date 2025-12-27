<div style="text-align: center;">
  <img width="200" height="200" src="https://github.com/prathieshna/BookFinder/blob/master/app/src/main/ic_launcher-playstore.png" alt="Book Finder app icon">
</div>

# Book Finder v1.7
Book Finder is an Ad Supported Mobile Application that uses Google Books API to get information about Books such as title, subtitle, description, cover image, ratings and reviews.

[Download Now!](https://play.google.com/store/apps/details?id=lk.prathieshna.bookfinder)

<br />
    
## Features
* Book Search 
* Detail View of a Book
* Add / Remove favourites
* Full / Partial Preview
* Read Reviews

## Recent Changes (v1.7)
- Implement edge-to-edge display and Open Library integration
- Update `targetSdk` to 36 and version to 1.7.
- Enable edge-to-edge display across activities using `enableEdgeToEdge()` and window inset listeners.
- Integrate Open Library API to fetch and display book community stats (ratings, reading status).
- Enhance book details with a new metadata section for publisher, page count, categories, and ISBN.
- Replace the single Google Books review button with a dual external links section for Google Books and Open Library.
- Add `androidx.activity:activity-ktx` dependency and `OpenLibraryService` retrofit implementation.
- Add `external_link_button_selector` drawable for stylized action buttons.
- Remove OpenLibraryRatings.kt, add API 27+ styles for display cutout support, and update layout strings and lint suppressions.

## Road Map
* Enhanced review features
* Book recommendations

### Authors
Prathieshna Vekneswaran
