// Adapts route paths to also include wildcard
// subroutes under the root level section.
export function pathToRegExp( path ) {
	// Prevents root level double dash urls from being validated.
	if ( path === '/' ) {
		return path;
	}
	return new RegExp( '^' + path + '(/.*)?$' );
}

// takes in a fn where its last arg is a node-style callback
// outputs a promise
export const promisify = ( fn ) => ( ...args ) =>
	new Promise( ( resolve, reject ) => {
		fn( ...args, ( err, data ) => {
			if ( err ) {
				reject( err );
			} else {
				resolve( data );
			}
		} );
	} );

/**
 * Wrap a promise so it can be cancelled.
 *
 * @param {Promise<T>} promise
 * @returns {{cancel(): void, promise: Promise<T>}}
 */
export function makeCancellable( promise ) {
	let cancellation = null;

	const wrappedPromise = new Promise( ( resolve, reject ) => {
		promise.then(
			( value ) => ( cancellation === null ? resolve( value ) : reject( cancellation ) ),
			( error ) => ( cancellation === null ? reject( error ) : reject( cancellation ) )
		);
	} );

	return {
		promise: wrappedPromise,
		cancel() {
			cancellation = new Error( 'Cancelled.' );
			cancellation.isCancelled = true;
		},
	};
}
